// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentFormatNotAcceptedException;
import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentIsNullException;
import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentTooLargeException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.AddedAttachment;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi.ApiApplicationsAddAttachmentPostMultipartForm;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.AttachFileResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class AttachFileToApplicationService {

    private static final Logger LOG = Logger.getLogger(AttachFileToApplicationService.class);

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;
    private final RemsApiQueryGateway gateway;
    private final Optional<Path> configuredAttachmentTempDirectory;

    private final ConcurrentHashMap<Path, Long> pendingCleanup = new ConcurrentHashMap<>();
    private final Set<Path> temporaryDirectories = ConcurrentHashMap.newKeySet();
    private ScheduledExecutorService cleanupExecutor;

    @PostConstruct
    void startCleanupRetries() {
        cleanupExecutor = Executors.newSingleThreadScheduledExecutor(task -> {
            var thread = new Thread(task, "ams-attachment-cleanup");
            thread.setDaemon(true);
            return thread;
        });
        cleanupExecutor.scheduleWithFixedDelay(this::retryPendingCleanup, 1, 1, TimeUnit.MINUTES);
    }

    @PreDestroy
    void stopCleanupRetries() {
        if (cleanupExecutor != null) {
            cleanupExecutor.shutdown();
        }
        retryPendingCleanup();
    }

    void retryPendingCleanup() {
        pendingCleanup.forEach((path, applicationId) -> {
            try {
                deleteTemporaryFile(path);
                pendingCleanup.remove(path, applicationId);
            } catch (IOException | RuntimeException e) {
                LOG.warnf(e,
                        "Attachment cleanup retry failed: applicationId=%s, stage=delete temporary file",
                        applicationId);
            }
        });
    }

    @Inject
    public AttachFileToApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @ConfigProperty(name = "daam.attachment-upload.temp-directory") Optional<String> configuredAttachmentTempDirectory,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi,
            RemsApiQueryGateway apiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
        this.gateway = apiQueryGateway;
        this.configuredAttachmentTempDirectory = configuredAttachmentTempDirectory
                .filter(path -> !path.isBlank())
                .map(Path::of);
    }

    public AttachFileToApplicationService(
            String remsApiKey,
            RemsApplicationCommandApi remsApplicationCommandApi,
            RemsApiQueryGateway apiQueryGateway
    ) {
        this(remsApiKey, Optional.empty(), remsApplicationCommandApi, apiQueryGateway);
    }

    public AddedAttachment attach(Long applicationId, String userId, String filename, File file) {
        gateway.checkIfApplicationIsEditableByUser(applicationId, userId);

        var attachmentResponse = submitFileToRems(applicationId, userId, filename, file);

        return AddedAttachment.builder()
                .id(attachmentResponse.getId())
                .build();
    }

    Path createTemporaryFile(Long applicationId, String filename) throws IOException {
        var directory = createTemporaryDirectory(applicationId);
        temporaryDirectories.add(directory);
        try {
            var basename = filename.replace('\\', '/');
            basename = basename.substring(basename.lastIndexOf('/') + 1);
            // Bound bytes as well as characters, retaining the extension used by REMS.
            basename = basename.replaceAll("[^a-zA-Z0-9._-]", "_");
            basename = basename.substring(Math.max(0, basename.length() - 100));
            return Files.createTempFile(directory, "upload-", "-" + basename);
        } catch (IOException | RuntimeException e) {
            try {
                deleteTemporaryFile(directory);
            } catch (IOException | RuntimeException cleanupError) {
                e.addSuppressed(cleanupError);
                // Creation failed before there was a file; retry removal of the directory.
                pendingCleanup.put(directory, applicationId);
            }
            throw e;
        }
    }

    private Path createTemporaryDirectory(Long applicationId) throws IOException {
        var candidates = new ArrayList<Path>();
        configuredAttachmentTempDirectory.ifPresent(candidates::add);
        candidates.add(Path.of(System.getProperty("java.io.tmpdir")));
        var userHome = System.getProperty("user.home");
        if (userHome != null && !userHome.isBlank()) {
            candidates.add(Path.of(userHome, ".gdi", "ams-attachment-tmp"));
        }
        IOException lastFailure = new IOException(
                "No attachment temp root directory candidates available");
        for (var candidate : candidates) {
            try {
                var supportsPosix = candidate.getFileSystem().supportedFileAttributeViews()
                        .contains(
                                "posix");
                FileAttribute<?>[] permissions = supportsPosix
                        ? new FileAttribute<?>[]{PosixFilePermissions.asFileAttribute(
                                PosixFilePermissions.fromString("rwx------"))}
                        : new FileAttribute<?>[0];
                Files.createDirectories(candidate, permissions);
                var directory = Files.createTempDirectory(candidate, "ams-attachment-",
                        permissions);
                if (!supportsPosix) {
                    try {
                        enforcePrivateAccessForDirectory(directory);
                    } catch (IOException aclError) {
                        try {
                            Files.deleteIfExists(directory);
                        } catch (IOException cleanupError) {
                            aclError.addSuppressed(cleanupError);
                        }
                        throw aclError;
                    }
                }
                return directory;
            } catch (IOException e) {
                LOG.warnf(e, "Attachment upload temp root unavailable: applicationId=%s, path=%s",
                        applicationId, candidate);
                lastFailure = e;
                if (configuredAttachmentTempDirectory.isPresent()
                        && configuredAttachmentTempDirectory.get().equals(candidate)) {
                    throw e;
                }
            }
        }
        throw lastFailure;
    }

    private void enforcePrivateAccessForDirectory(Path directory) throws IOException {
        var aclView = Files.getFileAttributeView(directory, AclFileAttributeView.class);
        if (aclView == null) {
            throw new IOException(
                    "Unable to enforce private access on attachment staging directory without POSIX or ACL support");
        }
        var owner = aclView.getOwner();
        var ownerOnlyAcl = AclEntry.newBuilder()
                .setType(AclEntryType.ALLOW)
                .setPrincipal(owner)
                .setPermissions(EnumSet.allOf(AclEntryPermission.class))
                .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
                .build();
        aclView.setAcl(List.of(ownerOnlyAcl));
    }

    void copyToTemporaryFile(File source, Path target) throws IOException {
        // Write into the existing inode to preserve createTempFile's restrictive permissions.
        try (var input = Files.newInputStream(source.toPath()); var output = Files.newOutputStream(
                target, StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            input.transferTo(output);
        }
    }

    void deleteTemporaryFile(Path path) throws IOException {
        Files.deleteIfExists(path);
        temporaryDirectories.remove(path);
        var directory = path.getParent();
        if (temporaryDirectories.contains(directory)) {
            Files.deleteIfExists(directory);
            temporaryDirectories.remove(directory);
        }
    }

    private AttachFileResponse submitFileToRems(
            Long applicationId,
            String userId,
            String filename,
            File file
    ) {
        if (filename == null || file == null) {
            throw new AttachmentIsNullException();
        }

        Path temporaryFile = null;
        var stage = "create temporary file";
        try {
            temporaryFile = createTemporaryFile(applicationId, filename);
            stage = "copy upload to temporary file (read source/write destination)";
            copyToTemporaryFile(file, temporaryFile);
            var multipartForm = new ApiApplicationsAddAttachmentPostMultipartForm();
            multipartForm._file = temporaryFile.toFile();
            stage = "forward attachment to REMS";
            return remsApplicationCommandApi.apiApplicationsAddAttachmentPost(
                    multipartForm, applicationId, remsApiKey, userId
            );
        } catch (WebApplicationException e) {
            LOG.errorf(e, "Attachment upload failed: applicationId=%s, stage=%s", applicationId,
                    stage);
            if (e.getResponse().getStatus() == Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode()) {
                throw new AttachmentTooLargeException(filename);
            }
            if (e.getResponse().getStatus() == Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode()) {
                throw new AttachmentFormatNotAcceptedException(filename);
            }
            throw new IllegalStateException(
                    "Unable to confirm attachment upload. Check the application attachments or contact support before uploading again.",
                    e);
        } catch (IOException | RuntimeException e) {
            LOG.errorf(e, "Attachment upload failed: applicationId=%s, stage=%s", applicationId,
                    stage);
            throw new IllegalStateException(
                    "Unable to confirm attachment upload. Check the application attachments or contact support before uploading again.",
                    e);
        } finally {
            // The REST client is synchronous; it has finished consuming the file here.
            if (temporaryFile != null) {
                try {
                    deleteTemporaryFile(temporaryFile);
                } catch (IOException | RuntimeException cleanupError) {
                    pendingCleanup.put(temporaryFile, applicationId);
                    LOG.warnf(cleanupError,
                            "Attachment cleanup failed: applicationId=%s, stage=delete temporary file",
                            applicationId);
                }
            }
        }
    }
}

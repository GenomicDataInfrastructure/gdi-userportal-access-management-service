// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

    @Inject
    public AttachFileToApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi,
            RemsApiQueryGateway apiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
        this.gateway = apiQueryGateway;
    }

    public AddedAttachment attach(Long applicationId, String userId, String filename, File file) {
        gateway.checkIfApplicationIsEditableByUser(applicationId, userId);

        var attachmentResponse = submitFileToRems(applicationId, userId, filename, file);

        return AddedAttachment.builder()
                .id(attachmentResponse.getId())
                .build();
    }

    Path createTemporaryFile(String filename) throws IOException {
        // Use only the basename: multipart filenames can contain client-side paths.
        var basename = filename.replace('\\', '/');
        basename = basename.substring(basename.lastIndexOf('/') + 1);
        return Files.createTempFile("ams-attachment-", "-" + basename);
    }

    void copyToTemporaryFile(File source, Path target) throws IOException {
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    void deleteTemporaryFile(Path path) throws IOException {
        Files.deleteIfExists(path);
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
            temporaryFile = createTemporaryFile(filename);
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
                    "Unable to upload attachment. Please retry or contact support.", e);
        } catch (IOException | RuntimeException e) {
            LOG.errorf(e, "Attachment upload failed: applicationId=%s, stage=%s", applicationId,
                    stage);
            throw new IllegalStateException(
                    "Unable to upload attachment. Please retry or contact support.", e);
        } finally {
            // The REST client is synchronous; it has finished consuming the file here.
            if (temporaryFile != null) {
                try {
                    deleteTemporaryFile(temporaryFile);
                } catch (IOException | RuntimeException cleanupError) {
                    LOG.warnf(cleanupError,
                            "Attachment cleanup failed: applicationId=%s, stage=delete temporary file",
                            applicationId);
                }
            }
        }
    }
}

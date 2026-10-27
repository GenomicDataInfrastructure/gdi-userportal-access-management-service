// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jboss.logmanager.ExtLogRecord;

import org.junit.jupiter.api.io.TempDir;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentFormatNotAcceptedException;
import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentIsNullException;
import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentTooLargeException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.AddedAttachment;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.AttachFileResponse;
import jakarta.ws.rs.WebApplicationException;
import lombok.SneakyThrows;

class AttachFileToApplicationServiceTest {

    private static final String REMS_API_KEY = "remsApiKey";
    private AttachFileToApplicationService underTest;
    private RemsApiQueryGateway gateway;
    private RemsApplicationCommandApi remsApplicationCommandApi;

    private final List<LogRecord> logs = new ArrayList<>();
    private final Logger logger = Logger.getLogger(AttachFileToApplicationService.class.getName());
    private final Handler logHandler = new Handler() {

        @Override
        public void publish(LogRecord record) {
            logs.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    };

    @AfterEach
    void removeLogHandler() {
        logger.removeHandler(logHandler);
        underTest.stopCleanupRetries();
    }

    @BeforeEach
    void setUp() {
        logger.addHandler(logHandler);
        gateway = mock(RemsApiQueryGateway.class);
        remsApplicationCommandApi = mock(RemsApplicationCommandApi.class);
        underTest = new AttachFileToApplicationService(
                REMS_API_KEY,
                remsApplicationCommandApi,
                gateway
        );
    }

    @Test
    void can_attach() {
        var applicationId = 1L;
        var userId = "userId";
        var attachment = attachment();

        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(
                any(),
                eq(applicationId),
                eq(REMS_API_KEY),
                eq(userId)
        )).thenReturn(AttachFileResponse.builder().id(1L).build());

        var actual = underTest.attach(applicationId, userId, attachment.getName(), attachment);

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(AddedAttachment.builder()
                        .id(1L)
                        .build());
    }

    @Test
    void throws_exception_if_filename_is_null() {
        var applicationId = 1L;
        var userId = "userId";
        var attachment = attachment();

        assertThrows(AttachmentIsNullException.class, () -> underTest.attach(
                applicationId, userId, null, attachment
        ));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
    }

    @Test
    void throws_exception_if_file_is_null() {
        var applicationId = 1L;
        var userId = "userId";

        assertThrows(AttachmentIsNullException.class, () -> underTest.attach(
                applicationId, userId, "not_null.txt", null
        ));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
    }

    @Test
    void throws_exception_if_remote_request_entity_too_large() {
        var applicationId = 1L;
        var userId = "userId";
        var attachment = attachment();

        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(
                any(),
                eq(applicationId),
                eq(REMS_API_KEY),
                eq(userId)
        )).thenThrow(new WebApplicationException(413));

        assertThrows(AttachmentTooLargeException.class, () -> underTest.attach(
                applicationId, userId, attachment.getName(), attachment
        ));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
    }

    @Test
    void throws_exception_if_remote_unsupported_media_type() {
        var applicationId = 1L;
        var userId = "userId";
        var attachment = attachment();

        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(
                any(),
                eq(applicationId),
                eq(REMS_API_KEY),
                eq(userId)
        )).thenThrow(new WebApplicationException(415));

        assertThrows(AttachmentFormatNotAcceptedException.class, () -> underTest.attach(
                applicationId, userId, attachment.getName(), attachment
        ));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
    }

    @Test
    void throws_not_expected_exception() {
        var applicationId = 1L;
        var userId = "userId";
        var attachment = attachment();

        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(
                any(),
                eq(applicationId),
                eq(REMS_API_KEY),
                eq(userId)
        )).thenThrow(new WebApplicationException(400));

        assertThrows(IllegalStateException.class, () -> underTest.attach(
                applicationId, userId, attachment.getName(), attachment
        ));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
    }

    @TempDir
    Path temporaryDirectory;

    @Test
    void keeps_copy_readable_until_rems_returns_then_deletes_it() throws IOException {
        var source = attachment();
        var forwarded = new AtomicReference<Path>();
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    RemsApplicationCommandApi.ApiApplicationsAddAttachmentPostMultipartForm form = invocation
                            .getArgument(0);
                    forwarded.set(form._file.toPath());
                    assertThat(form._file).hasBinaryContent(Files.readAllBytes(source.toPath()));
                    return AttachFileResponse.builder().id(1L).build();
                });

        underTest.attach(631L, "userId", "All-docs-for-gdi.pdf", source);

        assertThat(forwarded.get()).doesNotExist();
        assertThat(forwarded.get().getParent()).doesNotExist();
        assertThat(source).exists();
    }

    @Test
    void deletes_copy_after_rems_failure() {
        var forwarded = new AtomicReference<Path>();
        var failure = new WebApplicationException(500);
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenAnswer(invocation -> {
                    RemsApplicationCommandApi.ApiApplicationsAddAttachmentPostMultipartForm form = invocation
                            .getArgument(0);
                    forwarded.set(form._file.toPath());
                    assertThat(forwarded.get()).exists();
                    throw failure;
                });

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class).hasCause(failure);
        assertThat(forwarded.get()).doesNotExist();
        assertThat(forwarded.get().getParent()).doesNotExist();
        assertThat(logs).anySatisfy(record -> {
            assertThat(ExtLogRecord.wrap(record).getFormattedMessage())
                    .contains("applicationId=631",
                            "stage=forward attachment to REMS");
            assertThat(record.getThrown()).isSameAs(failure);
        });
    }

    @Test
    void reports_creation_failure_without_calling_rems() throws IOException {
        underTest = spy(underTest);
        var failure = new IOException("/private/internal/path");
        doThrow(failure).when(underTest).createTemporaryFile(any(), any());

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Unable to confirm attachment upload. Check the application attachments or contact support before uploading again.")
                .hasCause(failure);
        assertThat(logs).anySatisfy(record -> {
            assertThat(ExtLogRecord.wrap(record).getFormattedMessage())
                    .contains("applicationId=631",
                            "stage=create temporary file");
            assertThat(record.getThrown()).isSameAs(failure);
        });
        verifyNoInteractions(remsApplicationCommandApi);
    }

    @Test
    void deletes_partial_copy_after_write_failure() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any(), any());
        doThrow(new IOException("write failed")).when(underTest).copyToTemporaryFile(any(), any());

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class).hasCauseInstanceOf(IOException.class);
        assertThat(copy).doesNotExist();
        assertThat(logs).anySatisfy(record -> {
            assertThat(ExtLogRecord.wrap(record).getFormattedMessage())
                    .contains("applicationId=631", "stage=copy upload");
            assertThat(record.getThrown()).isInstanceOf(IOException.class);
        });
        verifyNoInteractions(remsApplicationCommandApi);
    }

    @Test
    void deletes_copy_after_source_read_failure() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any(), any());

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf",
                temporaryDirectory.resolve("missing.pdf").toFile()))
                .isInstanceOf(IllegalStateException.class).hasCauseInstanceOf(IOException.class);
        assertThat(copy).doesNotExist();
        assertThat(logs).anySatisfy(record -> {
            assertThat(ExtLogRecord.wrap(record).getFormattedMessage())
                    .contains("applicationId=631", "stage=copy upload");
            assertThat(record.getThrown()).isInstanceOf(IOException.class);
        });
        verifyNoInteractions(remsApplicationCommandApi);
    }

    @Test
    void cleanup_failure_does_not_replace_upload_failure() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any(), any());
        doThrow(new IOException("delete failed")).when(underTest).deleteTemporaryFile(any());
        var failure = new WebApplicationException(500);
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenThrow(failure);

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class).hasCause(failure);
        verify(underTest).deleteTemporaryFile(copy);
        assertThat(logs).anySatisfy(record -> {
            assertThat(ExtLogRecord.wrap(record).getFormattedMessage())
                    .contains("applicationId=631",
                            "stage=delete temporary file");
            assertThat(record.getThrown()).isInstanceOf(IOException.class);
        });
    }

    @Test
    void cleanup_failure_does_not_fail_successful_upload() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any(), any());
        doThrow(new IOException("delete failed")).when(underTest).deleteTemporaryFile(any());
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenReturn(AttachFileResponse.builder().id(1L).build());

        assertThat(underTest.attach(631L, "userId", "upload.pdf", attachment()).getId()).isEqualTo(
                1L);
        verify(underTest).deleteTemporaryFile(copy);
    }

    @Test
    void copy_preserves_private_permissions_and_directory() throws IOException {
        assumeTrue(temporaryDirectory.getFileSystem().supportedFileAttributeViews().contains(
                "posix"));
        var source = Files.writeString(temporaryDirectory.resolve("public.pdf"), "attachment");
        Files.setPosixFilePermissions(source, PosixFilePermissions.fromString("rw-r--r--"));
        var copy = underTest.createTemporaryFile(631L, "upload.pdf");
        var directory = copy.getParent();
        try {
            var filePermissions = Files.getPosixFilePermissions(copy);
            assertThat(filePermissions).isEqualTo(PosixFilePermissions.fromString("rw-------"));
            assertThat(Files.getPosixFilePermissions(directory))
                    .isEqualTo(PosixFilePermissions.fromString("rwx------"));
            underTest.copyToTemporaryFile(source.toFile(), copy);
            assertThat(copy).hasContent("attachment");
            assertThat(Files.getPosixFilePermissions(copy)).isEqualTo(filePermissions);
        } finally {
            underTest.deleteTemporaryFile(copy);
        }
        assertThat(directory).doesNotExist();
    }

    @Test
    void long_client_filename_is_bounded_and_keeps_extension() throws IOException {
        var copy = underTest.createTemporaryFile(631L, "C:\\client\\" + "é".repeat(300) + ".pdf");
        try {
            assertThat(copy.getFileName().toString()).endsWith(".pdf").hasSizeLessThan(150);
        } finally {
            underTest.deleteTemporaryFile(copy);
        }
        assertThat(copy.getParent()).doesNotExist();
    }

    @Test
    void uses_configured_temp_root_directory() throws IOException {
        var configuredRoot = Files.createDirectory(temporaryDirectory.resolve("upload-staging"));
        underTest = new AttachFileToApplicationService(
                REMS_API_KEY,
                Optional.of(configuredRoot.toString()),
                remsApplicationCommandApi,
                gateway
        );

        var copy = underTest.createTemporaryFile(631L, "upload.pdf");
        try {
            assertThat(copy.startsWith(configuredRoot)).isTrue();
        } finally {
            underTest.deleteTemporaryFile(copy);
        }
    }

    @Test
    void cleanup_retry_removes_retained_copy_and_private_directory() throws IOException {
        underTest = spy(underTest);
        var copy = underTest.createTemporaryFile(631L, "upload.pdf");
        doReturn(copy).when(underTest).createTemporaryFile(any(), any());
        doThrow(new IOException("temporarily unavailable")).doCallRealMethod()
                .when(underTest).deleteTemporaryFile(copy);
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenReturn(AttachFileResponse.builder().id(1L).build());

        underTest.attach(631L, "userId", "upload.pdf", attachment());
        assertThat(copy).exists();
        underTest.retryPendingCleanup();
        assertThat(copy).doesNotExist();
        assertThat(copy.getParent()).doesNotExist();
        underTest.retryPendingCleanup();
        verify(underTest, org.mockito.Mockito.times(2)).deleteTemporaryFile(copy);
    }

    @Test
    void repeated_cleanup_failures_remain_queued_until_success() throws IOException {
        underTest = spy(underTest);
        var copy = underTest.createTemporaryFile(631L, "upload.pdf");
        doReturn(copy).when(underTest).createTemporaryFile(any(), any());
        doThrow(new IOException("temporarily unavailable")).doThrow(new IOException(
                "still unavailable"))
                .doCallRealMethod().when(underTest).deleteTemporaryFile(copy);
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenThrow(new WebApplicationException(500));

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class);
        underTest.retryPendingCleanup();
        assertThat(copy).exists();
        underTest.retryPendingCleanup();
        assertThat(copy).doesNotExist();
        assertThat(copy.getParent()).doesNotExist();
    }

    @SneakyThrows
    private File attachment() {
        var classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("dummy.txt").getFile());
    }
}

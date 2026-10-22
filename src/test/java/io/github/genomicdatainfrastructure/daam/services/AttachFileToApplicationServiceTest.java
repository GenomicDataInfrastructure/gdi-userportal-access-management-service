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
import java.util.concurrent.atomic.AtomicReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
        assertThat(logs).anySatisfy(record -> {
            assertThat(record.getMessage()).contains("applicationId=631",
                    "stage=forward attachment to REMS");
            assertThat(record.getThrown()).isSameAs(failure);
        });
    }

    @Test
    void reports_creation_failure_without_calling_rems() throws IOException {
        underTest = spy(underTest);
        var failure = new IOException("/private/internal/path");
        doThrow(failure).when(underTest).createTemporaryFile(any());

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Unable to upload attachment. Please retry or contact support.")
                .hasCause(failure);
        assertThat(logs).anySatisfy(record -> {
            assertThat(record.getMessage()).contains("applicationId=631",
                    "stage=create temporary file");
            assertThat(record.getThrown()).isSameAs(failure);
        });
        verifyNoInteractions(remsApplicationCommandApi);
    }

    @Test
    void deletes_partial_copy_after_write_failure() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any());
        doThrow(new IOException("write failed")).when(underTest).copyToTemporaryFile(any(), any());

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class).hasCauseInstanceOf(IOException.class);
        assertThat(copy).doesNotExist();
        assertThat(logs).anySatisfy(record -> {
            assertThat(record.getMessage()).contains("applicationId=631", "stage=copy upload");
            assertThat(record.getThrown()).isInstanceOf(IOException.class);
        });
        verifyNoInteractions(remsApplicationCommandApi);
    }

    @Test
    void deletes_copy_after_source_read_failure() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any());

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf",
                temporaryDirectory.resolve("missing.pdf").toFile()))
                .isInstanceOf(IllegalStateException.class).hasCauseInstanceOf(IOException.class);
        assertThat(copy).doesNotExist();
        assertThat(logs).anySatisfy(record -> {
            assertThat(record.getMessage()).contains("applicationId=631", "stage=copy upload");
            assertThat(record.getThrown()).isInstanceOf(IOException.class);
        });
        verifyNoInteractions(remsApplicationCommandApi);
    }

    @Test
    void cleanup_failure_does_not_replace_upload_failure() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any());
        doThrow(new IOException("delete failed")).when(underTest).deleteTemporaryFile(any());
        var failure = new WebApplicationException(500);
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenThrow(failure);

        assertThatThrownBy(() -> underTest.attach(631L, "userId", "upload.pdf", attachment()))
                .isInstanceOf(IllegalStateException.class).hasCause(failure);
        verify(underTest).deleteTemporaryFile(copy);
        assertThat(logs).anySatisfy(record -> {
            assertThat(record.getMessage()).contains("applicationId=631",
                    "stage=delete temporary file");
            assertThat(record.getThrown()).isInstanceOf(IOException.class);
        });
    }

    @Test
    void cleanup_failure_does_not_fail_successful_upload() throws IOException {
        underTest = spy(underTest);
        var copy = Files.createFile(temporaryDirectory.resolve("upload.pdf"));
        doReturn(copy).when(underTest).createTemporaryFile(any());
        doThrow(new IOException("delete failed")).when(underTest).deleteTemporaryFile(any());
        when(remsApplicationCommandApi.apiApplicationsAddAttachmentPost(any(), any(), any(), any()))
                .thenReturn(AttachFileResponse.builder().id(1L).build());

        assertThat(underTest.attach(631L, "userId", "upload.pdf", attachment()).getId()).isEqualTo(
                1L);
        verify(underTest).deleteTemporaryFile(copy);
    }

    @SneakyThrows
    private File attachment() {
        var classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("dummy.txt").getFile());
    }
}

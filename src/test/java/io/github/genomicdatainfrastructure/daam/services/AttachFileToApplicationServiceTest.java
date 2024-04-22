// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

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

    @BeforeEach
    void setUp() {
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

        assertThrows(WebApplicationException.class, () -> underTest.attach(
                applicationId, userId, attachment.getName(), attachment
        ));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
    }

    @SneakyThrows
    private File attachment() {
        var classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("dummy.txt").getFile());
    }
}

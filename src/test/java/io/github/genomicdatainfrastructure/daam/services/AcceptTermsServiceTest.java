// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.AcceptTermsException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.AcceptTermsCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.AcceptLicensesCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SuccessResponse;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AcceptTermsServiceTest {

    private static final String REMS_API_KEY = "remsApiKey";
    private AcceptTermsService underTest;
    private RemsApiQueryGateway gateway;
    private RemsApplicationCommandApi remsApplicationCommandApi;

    @BeforeEach
    void setUp() {
        gateway = mock(RemsApiQueryGateway.class);
        remsApplicationCommandApi = mock(RemsApplicationCommandApi.class);
        underTest = new AcceptTermsService(
                REMS_API_KEY,
                remsApplicationCommandApi,
                gateway
        );
    }

    @Test
    void can_accept_terms() {
        var applicationId = 1L;
        var userId = "userId";
        AcceptTermsCommand command = new AcceptTermsCommand();
        command.setApplicationId(applicationId);
        command.setAcceptedLicenses(List.of(1L, 2L));

        SuccessResponse response = new SuccessResponse();
        response.setSuccess(true);

        when(remsApplicationCommandApi.apiApplicationsAcceptLicensesPost(
                eq(REMS_API_KEY), eq(userId), any(AcceptLicensesCommand.class)))
                .thenReturn(response);

        underTest.acceptTerms(applicationId, userId, command);

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
        verify(remsApplicationCommandApi).apiApplicationsAcceptLicensesPost(
                eq(REMS_API_KEY), eq(userId), any(AcceptLicensesCommand.class));
    }

    @Test
    void throws_exception_when_accept_terms_fails() {
        var applicationId = 1L;
        var userId = "userId";
        AcceptTermsCommand command = new AcceptTermsCommand();
        command.setApplicationId(applicationId);
        command.setAcceptedLicenses(List.of(1L, 2L));

        SuccessResponse response = new SuccessResponse();
        response.setSuccess(false);
        response.setErrors(List.of("Error 1", "Error 2"));

        when(remsApplicationCommandApi.apiApplicationsAcceptLicensesPost(
                eq(REMS_API_KEY), eq(userId), any(AcceptLicensesCommand.class)))
                .thenReturn(response);

        assertThrows(AcceptTermsException.class, () -> underTest.acceptTerms(applicationId, userId,
                command));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
        verify(remsApplicationCommandApi).apiApplicationsAcceptLicensesPost(
                eq(REMS_API_KEY), eq(userId), any(AcceptLicensesCommand.class));
    }

    @Test
    void throws_web_application_exception() {
        var applicationId = 1L;
        var userId = "userId";
        AcceptTermsCommand command = new AcceptTermsCommand();
        command.setApplicationId(applicationId);
        command.setAcceptedLicenses(List.of(1L, 2L));

        when(remsApplicationCommandApi.apiApplicationsAcceptLicensesPost(
                eq(REMS_API_KEY), eq(userId), any(AcceptLicensesCommand.class)))
                .thenThrow(new WebApplicationException(400));

        assertThrows(WebApplicationException.class, () -> underTest.acceptTerms(applicationId,
                userId, command));

        verify(gateway).checkIfApplicationIsEditableByUser(applicationId, userId);
        verify(remsApplicationCommandApi).apiApplicationsAcceptLicensesPost(
                eq(REMS_API_KEY), eq(userId), any(AcceptLicensesCommand.class));
    }
}

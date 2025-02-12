// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import io.github.genomicdatainfrastructure.daam.model.InviteMember;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationQueryApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.InviteMemberCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.InviteMemberResponse;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Response10953InvitedMembers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InviteMemberServiceTest {

    private static final String remsApiKey = "remsApiKey";
    private final Long applicationId = 1L;
    private final String userId = "1";

    private RemsApplicationCommandApi remsApplicationCommandApi;
    private RemsApplicationQueryApi remsApplicationQueryApi;
    private InviteMemberService service;

    @BeforeEach
    void setUp() {
        remsApplicationCommandApi = mock(RemsApplicationCommandApi.class);
        remsApplicationQueryApi = mock(RemsApplicationQueryApi.class);
        service = new InviteMemberService(remsApiKey, remsApplicationCommandApi);
    }

    @Test
    void inviteMember() {
        when(remsApplicationQueryApi.apiApplicationsApplicationIdGet(
                remsApiKey,
                userId,
                applicationId))
                .thenReturn(Application.builder().build());

        var inviteMemberCommand = InviteMemberCommand.builder()
                .applicationId(applicationId)
                .member(new Response10953InvitedMembers("John", "john@genomicdata.com"))
                .build();

        when(remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                remsApiKey,
                userId,
                inviteMemberCommand))
                .thenReturn(new InviteMemberResponse(true, List.of(), List.of()));

        service.invite(applicationId, userId, new InviteMember("John", "john@genomicdata.com"));

        verify(remsApplicationCommandApi).apiApplicationsInviteMemberPost(
                eq(remsApiKey),
                eq(userId),
                eq(inviteMemberCommand));
    }

    @Test
    void whenRemsReturnsNotFound_inviteMember_shouldThrowNotFoundException() {
        when(remsApplicationQueryApi.apiApplicationsApplicationIdGet(
                remsApiKey,
                userId,
                applicationId))
                .thenReturn(Application.builder().build());

        when(remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                remsApiKey,
                userId,
                new InviteMemberCommand(applicationId, new Response10953InvitedMembers("John",
                        "john@genomicdata.com"))))
                .thenReturn(new InviteMemberResponse(false, List.of(Map.of("type",
                        "application-not-found")), List.of()));

        assertThatThrownBy(
                () -> service.invite(applicationId, userId, new InviteMember("John",
                        "john@genomicdata.com"))
        ).isInstanceOf(ApplicationNotFoundException.class)
                .hasMessage("The application with Id 1 was not found.");
    }

    @Test
    void whenRemsReturnsForbidden_inviteMember_shouldThrowApplicationDoesNotBelongToUser() {
        when(remsApplicationQueryApi.apiApplicationsApplicationIdGet(
                remsApiKey,
                userId,
                applicationId))
                .thenReturn(Application.builder().build());

        when(remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                remsApiKey,
                userId,
                new InviteMemberCommand(applicationId, new Response10953InvitedMembers("John",
                        "john@genomicdata.com"))))
                .thenReturn(new InviteMemberResponse(false, List.of(Map.of("type", "forbidden")),
                        List.of()));

        assertThatThrownBy(
                () -> service.invite(applicationId, userId, new InviteMember("John",
                        "john@genomicdata.com"))
        )
                .isInstanceOf(UserNotApplicantException.class)
                .hasMessage("The user 1 cannot add to application 1");
    }
}
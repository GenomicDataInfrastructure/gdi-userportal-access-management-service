// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.exceptions.MemberNotInvitedException;
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
import static org.mockito.Mockito.*;

class InviteMemberServiceTest {

    private static final String REMS_API_KEY = "remsApiKey";
    private final Long applicationId = 1L;
    private final String userId = "1";

    private RemsApplicationCommandApi remsApplicationCommandApi;
    private RemsApplicationQueryApi remsApplicationQueryApi;
    private InviteMemberService service;

    @BeforeEach
    void setUp() {
        remsApplicationCommandApi = mock(RemsApplicationCommandApi.class);
        remsApplicationQueryApi = mock(RemsApplicationQueryApi.class);
        service = new InviteMemberService(REMS_API_KEY, remsApplicationCommandApi);
    }

    @Test
    void inviteMember() {
        when(remsApplicationQueryApi.apiApplicationsApplicationIdGet(
                REMS_API_KEY,
                userId,
                applicationId))
                .thenReturn(Application.builder().build());

        var inviteMemberCommand = InviteMemberCommand.builder()
                .applicationId(applicationId)
                .member(new Response10953InvitedMembers("John", "john@genomicdata.com"))
                .build();

        when(remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                REMS_API_KEY,
                userId,
                inviteMemberCommand))
                .thenReturn(new InviteMemberResponse(true, List.of(), List.of()));

        service.invite(applicationId, userId, new InviteMember("John", "john@genomicdata.com"));

        verify(remsApplicationCommandApi).apiApplicationsInviteMemberPost(
                REMS_API_KEY,
                userId,
                inviteMemberCommand);
    }

    @Test
    void whenRemsReturnsNotFound_inviteMember_shouldThrowNotFoundException() {
        when(remsApplicationQueryApi.apiApplicationsApplicationIdGet(
                REMS_API_KEY,
                userId,
                applicationId))
                .thenReturn(Application.builder().build());

        when(remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                REMS_API_KEY,
                userId,
                new InviteMemberCommand(applicationId, new Response10953InvitedMembers("John",
                        "john@genomicdata.com"))))
                .thenReturn(new InviteMemberResponse(false, List.of(Map.of("type",
                        "application-not-found")), List.of()));

        var invitedMember = new InviteMember("John", "john@genomicdata.com");

        assertThatThrownBy(
                () -> service.invite(applicationId, userId, invitedMember)
        ).isInstanceOf(ApplicationNotFoundException.class)
                .hasMessage("The application with Id 1 was not found.");
    }

    @Test
    void whenRemsReturnsForbidden_inviteMember_shouldThrowApplicationDoesNotBelongToUser() {
        when(remsApplicationQueryApi.apiApplicationsApplicationIdGet(
                REMS_API_KEY,
                userId,
                applicationId))
                .thenReturn(Application.builder().build());

        when(remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                REMS_API_KEY,
                userId,
                new InviteMemberCommand(applicationId, new Response10953InvitedMembers("John",
                        "inviteMember@genomicdata.com"))))
                .thenReturn(new InviteMemberResponse(false, List.of(Map.of("type", "forbidden")),
                        List.of()));

        InviteMember invitedMember = new InviteMember("John",
                "inviteMember@genomicdata.com");

        assertThatThrownBy(
                () -> service.invite(applicationId, userId, invitedMember)
        )
                .isInstanceOf(UserNotApplicantException.class)
                .hasMessage("The user 1 cannot add to application 1");
    }

    @Test
    void whenRemsReturnsFailure_inviteMember_shouldThrowMemberNotInvitedException() {
        when(remsApplicationQueryApi.apiApplicationsApplicationIdGet(
                REMS_API_KEY,
                userId,
                applicationId))
                .thenReturn(Application.builder().build());

        when(remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                REMS_API_KEY,
                userId,
                new InviteMemberCommand(applicationId, new Response10953InvitedMembers("John",
                        "john@genomicdata.com"))))
                .thenReturn(new InviteMemberResponse(
                        false,
                        List.of(Map.of("type", "unknown")),
                        List.of()));

        InviteMember invitedMember = new InviteMember("John",
                "john@genomicdata.com");

        assertThatThrownBy(
                () -> service.invite(applicationId, userId, invitedMember)
        )
                .isInstanceOf(MemberNotInvitedException.class)
                .hasMessage("Member John could not be invited");
    }
}
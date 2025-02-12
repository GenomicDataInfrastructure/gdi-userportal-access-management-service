// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.exceptions.MemberNotInvitedException;
import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import io.github.genomicdatainfrastructure.daam.model.InviteMember;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.InviteMemberCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.InviteMemberResponse;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Response10953InvitedMembers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;

@ApplicationScoped
public class InviteMemberService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;

    private static final Map<String, BiFunction<Long, String, RuntimeException>> EXCEPTION_MAP = Map
            .of(
                    "application-not-found",
                    (applicationID, username) -> new ApplicationNotFoundException(applicationID),
                    "forbidden",
                    (applicationID, username) -> new UserNotApplicantException(username,
                            applicationID)
            );

    @Inject
    public InviteMemberService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
    }

    @Retry(maxRetries = 5, retryOn = MemberNotInvitedException.class, delay = 1000, delayUnit = ChronoUnit.MILLIS)
    public void invite(final Long applicationId, final String existingUserId,
            final InviteMember member) {
        var remsMember = new Response10953InvitedMembers(member.getName(), member.getEmail());
        var remsCommand = new InviteMemberCommand(applicationId, remsMember);

        var response = remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                remsApiKey, existingUserId, remsCommand
        );

        if (!response.getSuccess()) {
            handleFailedInvite(response, applicationId, existingUserId, remsMember.getName());
        }
    }

    private void handleFailedInvite(final InviteMemberResponse response, final Long applicationId,
            final String existingUserId, final String invitedUserName) {

        throw EXCEPTION_MAP.getOrDefault(extractErrorType(invitedUserName, response.getErrors()),
                (appId, userId) -> new MemberNotInvitedException(invitedUserName))
                .apply(applicationId, existingUserId);
    }

    private static String extractErrorType(String invitedUserName,
            List<Map<String, Object>> errors) {
        if (errors == null || errors.isEmpty()) {
            throw new MemberNotInvitedException(invitedUserName);
        }
        return errors.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .map(m -> m.get("type"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseThrow(() -> new MemberNotInvitedException(invitedUserName));
    }
}

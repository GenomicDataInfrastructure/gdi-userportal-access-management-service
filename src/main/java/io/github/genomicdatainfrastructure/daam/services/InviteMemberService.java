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

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@ApplicationScoped
public class InviteMemberService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;

    private static final Map<String, BiFunction<String, String, RuntimeException>> EXCEPTION_MAP = Map.of(
            "application-not-found", (userId, memberName) -> new ApplicationNotFoundException(),
            "forbidden", (userId, memberName) -> new UserNotApplicantException(userId)
    );

    @Inject
    public InviteMemberService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
    }

    @Retry(maxRetries = 3, retryOn = MemberNotInvitedException.class)
    public void invite(Long applicationId, String userId, InviteMember member) {
        var remsMember = new Response10953InvitedMembers(member.getName(), member.getEmail());
        var remsCommand = new InviteMemberCommand(applicationId, remsMember);

        var response = remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                remsApiKey, userId, remsCommand
        );

        if (!response.getSuccess()) {
            mapToException(response, userId, remsMember.getName());
        }
    }

    private void mapToException(InviteMemberResponse response, String userId, String username) {
        List<Map<String, Object>> errors = response.getErrors();

        if (errors == null || errors.isEmpty()) {
            throw new MemberNotInvitedException(username);
        }

        Map<String, Object> firstError = errors.get(0);
        String errorType = (String) firstError.get("type");

        throw EXCEPTION_MAP.getOrDefault(errorType, (id, memberName) -> new MemberNotInvitedException(memberName)).apply(userId, username);
    }
}

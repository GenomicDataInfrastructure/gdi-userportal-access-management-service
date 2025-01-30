// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.exceptions.MemberNotInvitedException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.InviteMember;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationQueryApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.InviteMemberCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Response10953InvitedMembers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class InviteMemberService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;
    private final RemsApiQueryGateway remsApiQueryGateway;

    @Inject
    public InviteMemberService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi, RemsApiQueryGateway remsApiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
        this.remsApiQueryGateway = remsApiQueryGateway;
    }

    public void invite(Long applicationId, String userId, InviteMember member) {
        var name = member.getName();
        var email = member.getEmail();

        var remsMember = new Response10953InvitedMembers(name, email);
        var remsCommand = new InviteMemberCommand(applicationId, remsMember);

        var response = remsApplicationCommandApi.apiApplicationsInviteMemberPost(
                remsApiKey, userId, remsCommand
        );

        if (!remsApiQueryGateway.doesApplicationExist(applicationId, userId)) {
            throw new ApplicationNotFoundException();
        }

        if (Boolean.FALSE.equals(response.getSuccess())) {
            throw new MemberNotInvitedException(name);
        }
    }
}

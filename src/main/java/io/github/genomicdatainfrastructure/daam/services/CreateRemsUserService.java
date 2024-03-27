// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsUserCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateUserCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class CreateRemsUserService {

    private final String remsApiKey;
    private final String remsBotUser;
    private final RemsUserCommandApi remsUserCommandApi;

    @Inject
    public CreateRemsUserService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.bot-user") String remsBotUser,
            @RestClient RemsUserCommandApi remsUserCommandApi
    ) {
        this.remsApiKey = remsApiKey;
        this.remsBotUser = remsBotUser;
        this.remsUserCommandApi = remsUserCommandApi;
    }

    public void createRemsUser(String userId, String name, String email) {
        var createUser = CreateUserCommand.builder()
                .userid(userId)
                .name(name)
                .email(email)
                .build();

        remsUserCommandApi.apiUsersCreatePost(remsApiKey, remsBotUser, createUser);
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class CreateApplicationService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;

    @Inject
    public CreateApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi applicationsApi
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = applicationsApi;
    }

    public void createRemsApplication(CreateApplication createApplication, String userId) {
        var command = CreateApplicationCommand.builder()
                .catalogueItemIds(createApplication.getDatasetIds())
                .build();

        remsApplicationCommandApi.apiApplicationsCreatePost(command, remsApiKey, userId);
    }
}
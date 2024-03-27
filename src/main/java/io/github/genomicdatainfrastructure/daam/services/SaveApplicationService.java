// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.gateways.SaveDraftCommandMapper;
import io.github.genomicdatainfrastructure.daam.model.SaveFormsAndDuos;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SaveApplicationService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;
    private final RemsApiQueryGateway gateway;

    @Inject
    public SaveApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi,
            RemsApiQueryGateway apiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
        this.gateway = apiQueryGateway;
    }

    public void save(String userId, Long applicationId, SaveFormsAndDuos saveFormsAndDuos) {
        gateway.checkIfApplicationIsEditableByUser(applicationId, userId);
        var command = SaveDraftCommandMapper.from(applicationId, saveFormsAndDuos);
        remsApplicationCommandApi.apiApplicationsSaveDraftPost(remsApiKey, userId, command);
    }
}

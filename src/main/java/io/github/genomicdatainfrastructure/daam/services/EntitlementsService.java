// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationQueryApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.EntitlementResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EntitlementsService {

    private static final Logger LOG = Logger.getLogger(EntitlementsService.class);

    private final String remsApiKey;
    private final RemsApplicationQueryApi remsApplicationQueryApi;

    @Inject
    public EntitlementsService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationQueryApi remsApplicationQueryApi
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationQueryApi = remsApplicationQueryApi;
    }

    public EntitlementResponse getGrantedDatasetIdentifiers(String userId) {
        LOG.info("API Key: " + remsApiKey);
        LOG.info("User ID: " + userId);
        return remsApplicationQueryApi.apiEntitlementsGet(remsApiKey, userId, null, null, false);
    }
}
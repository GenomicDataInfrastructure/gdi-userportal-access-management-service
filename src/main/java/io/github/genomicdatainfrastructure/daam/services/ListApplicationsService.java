// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationQueryApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.ApplicationOverview;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class ListApplicationsService {

    private final String remsApiKey;
    private final RemsApplicationQueryApi remsApplicationQueryApi;

    @Inject
    public ListApplicationsService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationQueryApi remsApplicationQueryApi
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationQueryApi = remsApplicationQueryApi;
    }

    public List<ListedApplication> listApplications(String userId) {
        return remsApplicationQueryApi.apiMyApplicationsGet(remsApiKey, userId, null).stream()
                .map(this::parse)
                .toList();
    }

    public ListedApplication parse(ApplicationOverview applicationOverview) {
        return ListedApplication.builder()
                .id(applicationOverview.getApplicationId())
                .title(applicationOverview.getApplicationExternalId())
                .currentState(applicationOverview.getApplicationState().value())
                .stateChangedAt(applicationOverview.getApplicationLastActivity())
                .build();
    }
}

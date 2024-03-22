// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.mappers.RemsApplicationMapper;
import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationsApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@ApplicationScoped
public class RetrieveApplicationService {
    private final String remsApiKey;
    private final RemsApplicationsApi applicationsApi;

    @Inject
    public RetrieveApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationsApi applicationsApi
    ) {
        this.remsApiKey = remsApiKey;
        this.applicationsApi = applicationsApi;
    }

    public RetrievedApplication retrieveApplication(Long applicationId, String userId) {
        try {
            return RemsApplicationMapper
                    .toRetrievedApplication(applicationsApi.apiApplicationsApplicationIdGet(applicationId, remsApiKey, userId));
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ApplicationNotFoundException(applicationId);
            }
            throw e;
        }
    }
}

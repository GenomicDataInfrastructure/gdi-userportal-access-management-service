// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationsApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.ApplicationOverview;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class RemsApiQueryGateway {

    private final String remsApiKey;
    private final RemsApplicationsApi applicationsApi;

    public RemsApiQueryGateway(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationsApi applicationsApi
    ) {
        this.remsApiKey = remsApiKey;
        this.applicationsApi = applicationsApi;
    }

    public Application retrieveApplication(Long applicationId, String userId) {
        try {
            return applicationsApi.apiApplicationsApplicationIdGet(
                    applicationId, remsApiKey, userId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ApplicationNotFoundException(applicationId);
            }
            throw e;
        }
    }

    public List<ApplicationOverview> listApplications(String userId) {
        return applicationsApi.apiMyApplicationsGet(remsApiKey, userId, null);
    }
}

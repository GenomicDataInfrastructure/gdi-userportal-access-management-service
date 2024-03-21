// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;
import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.model.RemsApplicationMapper;
import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationsApi;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;


@ApplicationScoped
public class RetrieveApplicationService {
    private final String remsApiKey;
    private final RemsApplicationsApi applicationsApi;
    private final SecurityIdentity identity;

    @Inject
    public RetrieveApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            SecurityIdentity identity,
            @RestClient RemsApplicationsApi applicationsApi
    ) {
        this.remsApiKey = remsApiKey;
        this.applicationsApi = applicationsApi;
        this.identity = identity;
    }

    public RetrievedApplication retrieveApplication(Long applicationId) {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        String userId = principal.getClaim(USER_ID_CLAIM);

        try {
            return RemsApplicationMapper
                    .toRetrievedApplication(applicationsApi.apiApplicationsApplicationIdGet(applicationId, remsApiKey, userId));
        }
        catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ApplicationNotFoundException(applicationId);
            }
            throw e;
        }
    }
}

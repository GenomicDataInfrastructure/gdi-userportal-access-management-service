// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;
import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.ApplicationsApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationCommand;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CreateApplicationsService {

    private final SecurityIdentity identity;
    private final String remsApiKey;
    private final ApplicationsApi applicationsApi;

    @Inject
    public CreateApplicationsService(
        @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
        SecurityIdentity identity,
        @RestClient ApplicationsApi applicationsApi
    ) {
        this.remsApiKey = remsApiKey;
        this.identity = identity;
        this.applicationsApi = applicationsApi;
    }

    public void createRemsApplication(CreateApplication createApplication) {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        String userId = principal.getClaim(USER_ID_CLAIM);

        List<Long> catalogueItemIds = createApplication.getDatasetIds().stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        
        CreateApplicationCommand command = CreateApplicationCommand.builder()
                .catalogueItemIds(catalogueItemIds)
                .build();
    
        applicationsApi.apiApplicationsCreatePost(command, remsApiKey, userId);
    }
}
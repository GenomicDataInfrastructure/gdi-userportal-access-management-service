// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SubmitApplicationCommand;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class SubmitApplicationService {

    private final SecurityIdentity identity;
    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;

    @Inject
    public SubmitApplicationService(
        @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
        SecurityIdentity identity,
        @RestClient RemsApplicationCommandApi applicationsApi
    ) {
        this.remsApiKey = remsApiKey;
        this.identity = identity;
        this.remsApplicationCommandApi = applicationsApi;
    }

    public void submitApplication(String id) {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        String userId = principal.getClaim(USER_ID_CLAIM);

        SubmitApplicationCommand command = SubmitApplicationCommand.builder()
                .applicationId(Long.valueOf(id))
                .build();

        remsApplicationCommandApi.apiApplicationsSubmitPost(command, remsApiKey, userId);
    }
}

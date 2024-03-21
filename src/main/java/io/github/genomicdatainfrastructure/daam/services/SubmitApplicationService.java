// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationsApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SubmitApplicationCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.ApplicationOverview.ApplicationStateEnum;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class SubmitApplicationService {

    private final SecurityIdentity identity;
    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;
    private final RemsApplicationsApi remsApplicationsApi;

    @Inject
    public SubmitApplicationService(
        @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
        SecurityIdentity identity,
        @RestClient RemsApplicationCommandApi applicationCommandApi,
        @RestClient RemsApplicationsApi applicationsApi
    ) {
        this.remsApiKey = remsApiKey;
        this.identity = identity;
        this.remsApplicationCommandApi = applicationCommandApi;
        this.remsApplicationsApi = applicationsApi;
    }

    public void submitApplication(Long id) {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        String userId = principal.getClaim(USER_ID_CLAIM);

        checkApplication(id, userId);

        SubmitApplicationCommand command = SubmitApplicationCommand.builder()
                .applicationId(id)
                .build();

        remsApplicationCommandApi.apiApplicationsSubmitPost(command, remsApiKey, userId);
    }

    private void checkApplication(Long id, String userId) {
        try {
            var application = remsApplicationsApi.apiApplicationsApplicationIdGet(id, remsApiKey, userId);

            if (!application.getApplicationApplicant().getUserid().equals(userId)) {
                throw new UserNotApplicantException(id, userId);
            }

            if (!application.getApplicationState().equals(ApplicationStateEnum.DRAFT) && !application.getApplicationState().equals(ApplicationStateEnum.RETURNED)) {
                throw new ApplicationNotInCorrectStateException(id, application.getApplicationState().value());
            }
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ApplicationNotFoundException(id);
            }

            throw e;
        }

    }

}

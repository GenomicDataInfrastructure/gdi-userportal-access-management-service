// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.AcceptLicensesCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

@ApplicationScoped
public class AcceptTermsService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;
    private final RemsApiQueryGateway remsApiQueryGateway;

    @Inject
    public AcceptTermsService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi,
            RemsApiQueryGateway remsApiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
        this.remsApiQueryGateway = remsApiQueryGateway;
    }

    public void acceptTerms(Long id, String userId, AcceptLicensesCommand acceptLicensesCommand) {
        Application application = remsApiQueryGateway.retrieveApplication(id, userId);
        if (!application.getApplicationApplicant().getUserid().equals(userId)) {
            throw new UserNotApplicantException(id, userId);
        }

        try {
            remsApplicationCommandApi.apiApplicationsAcceptLicensesPost(remsApiKey, userId,
                    acceptLicensesCommand);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Status.NOT_FOUND.getStatusCode()) {
                throw new ApplicationNotFoundException(id);
            }
            if (e.getResponse().getStatus() == Status.FORBIDDEN.getStatusCode()) {
                throw new UserNotApplicantException(id, userId);
            }
            if (e.getResponse().getStatus() == Status.PRECONDITION_REQUIRED.getStatusCode()) {
                throw new ApplicationNotInCorrectStateException(id,
                        "Application not in submittable state");
            }
            throw e;
        }
    }
}

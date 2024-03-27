// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application.ApplicationStateEnum;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SubmitApplicationCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Set;

@ApplicationScoped
public class SubmitApplicationService {

    private static final Set<ApplicationStateEnum> VALID_STATES_FOR_SUBMISSION = Set.of(
            ApplicationStateEnum.DRAFT,
            ApplicationStateEnum.RETURNED
    );

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;
    private final RemsApiQueryGateway gateway;

    @Inject
    public SubmitApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi applicationCommandApi,
            RemsApiQueryGateway remsApiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = applicationCommandApi;
        this.gateway = remsApiQueryGateway;
    }

    public void submitApplication(Long id, String userId) {
        checkApplication(id, userId);

        SubmitApplicationCommand command = SubmitApplicationCommand.builder().applicationId(id)
                .build();

        remsApplicationCommandApi.apiApplicationsSubmitPost(command, remsApiKey, userId);
    }

    private void checkApplication(Long id, String userId) {
        var application = gateway.retrieveApplication(id, userId);

        if (!application.getApplicationApplicant().getUserid().equals(userId)) {
            throw new UserNotApplicantException(id, userId);
        }

        if (!VALID_STATES_FOR_SUBMISSION.contains(application.getApplicationState())) {
            throw new ApplicationNotInCorrectStateException(
                    id,
                    application.getApplicationState().value()
            );
        }
    }
}

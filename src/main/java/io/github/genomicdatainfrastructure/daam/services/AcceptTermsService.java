// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.AcceptTermsException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.AcceptTermsCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.AcceptLicensesCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SuccessResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import static java.util.Optional.ofNullable;

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

    public void acceptTerms(Long id, String userId, AcceptTermsCommand acceptTermsCommand) {
        remsApiQueryGateway.checkIfApplicationIsEditableByUser(id, userId);

        AcceptLicensesCommand remoteAcceptLicensesCommand = new AcceptLicensesCommand();
        remoteAcceptLicensesCommand.setApplicationId(acceptTermsCommand.getApplicationId());
        remoteAcceptLicensesCommand.setAcceptedLicenses(acceptTermsCommand.getAcceptedLicenses());

        SuccessResponse response = remsApplicationCommandApi.apiApplicationsAcceptLicensesPost(
                remsApiKey, userId, remoteAcceptLicensesCommand);

        if (Boolean.FALSE.equals(response.getSuccess())) {
            var nonNullErrors = ofNullable(response.getErrors()).orElseGet(List::of);

            List<String> errorStrings = nonNullErrors.stream()
                    .map(Object::toString)
                    .toList();

            throw new AcceptTermsException(id, errorStrings);
        }
    }
}

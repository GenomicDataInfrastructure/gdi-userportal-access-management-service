// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.AcceptTermsException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.AcceptTermsCommand;
import io.github.genomicdatainfrastructure.daam.model.ValidationWarning;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.AcceptLicensesCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@Log
@ApplicationScoped
public class AcceptTermsService {

    private static final String ACCEPT_TERMS_LOG = "Terms and Licenses of application %s could not be accepted: %s";

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

        var remoteAcceptLicensesCommand = AcceptLicensesCommand.builder()
                .applicationId(id)
                .acceptedLicenses(acceptTermsCommand.getAcceptedLicenses())
                .build();

        var response = remsApplicationCommandApi.apiApplicationsAcceptLicensesPost(
                remsApiKey, userId, remoteAcceptLicensesCommand);

        if (Boolean.FALSE.equals(response.getSuccess())) {
            var nonNullErrors = ofNullable(response.getErrors()).orElseGet(List::of);

            var concatenatedErrors = nonNullErrors.stream()
                    .map(Object::toString)
                    .collect(joining(";"));

            log.warning(ACCEPT_TERMS_LOG.formatted(id, concatenatedErrors));

            var warnings = nonNullErrors.stream()
                    .map(it -> ValidationWarning.builder()
                            .key(it.getType())
                            .formId(it.getFormId())
                            .fieldId(it.getFieldId())
                            .build())
                    .toList();

            throw new AcceptTermsException(warnings);
        }
    }
}

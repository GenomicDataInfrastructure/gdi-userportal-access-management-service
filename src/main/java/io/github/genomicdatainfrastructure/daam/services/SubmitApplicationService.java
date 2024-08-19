// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationSubmissionException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.ValidationWarning;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SubmitApplicationCommand;
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
public class SubmitApplicationService {

    private static final String SUBMISSION_LOG = "%s failed to submit application %s: %s";

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
        gateway.checkIfApplicationIsEditableByUser(id, userId);

        var command = SubmitApplicationCommand.builder()
                .applicationId(id)
                .build();

        var response = remsApplicationCommandApi.apiApplicationsSubmitPost(
                remsApiKey, userId,
                command);

        if (Boolean.FALSE.equals(response.getSuccess())) {
            var nonNullErrors = ofNullable(response.getErrors()).orElseGet(List::of);

            var concatenatedErrors = nonNullErrors.stream()
                    .map(Object::toString)
                    .collect(joining(";"));

            log.warning(SUBMISSION_LOG.formatted(userId, id, concatenatedErrors));

            var warnings = nonNullErrors.stream()
                    .map(it -> ValidationWarning.builder()
                            .key(it.getType())
                            .formId(it.getFormId())
                            .fieldId(it.getFieldId())
                            .build())
                    .toList();

            throw new ApplicationSubmissionException(warnings);
        }
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.mappers.RemsApplicationMapper;
import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationsApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application;
import io.github.genomicdatainfrastructure.daam.utils.ApplicationValidator;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class RetrieveApplicationService {

    private final String remsApiKey;
    private final RemsApplicationsApi applicationsApi;
    private final SecurityIdentity identity;
    private RemsApplicationMapper applicationMapper;
    private final ApplicationValidator applicationValidator;

    @Inject
    public RetrieveApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationsApi applicationsApi,
            SecurityIdentity identity,
            RemsApplicationMapper applicationMapper,
            ApplicationValidator applicationValidator
    ) {
        this.remsApiKey = remsApiKey;
        this.applicationsApi = applicationsApi;
        this.identity = identity;
        this.applicationMapper = applicationMapper;
        this.applicationValidator = applicationValidator;
    }

    public RetrievedApplication retrieveApplication(Long applicationId, String userId) {
        Application application = applicationValidator.validate(applicationId, remsApiKey, userId,
                applicationsApi);

        return applicationMapper.from(application);
    }
}

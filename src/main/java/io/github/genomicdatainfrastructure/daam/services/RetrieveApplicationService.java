// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.mappers.RemsApplicationMapper;
import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RetrieveApplicationService {

    private RemsApplicationMapper applicationMapper;
    private final RemsApiQueryGateway gateway;

    @Inject
    public RetrieveApplicationService(
            RemsApplicationMapper applicationMapper,
            RemsApiQueryGateway remsApiQueryGateway
    ) {
        this.applicationMapper = applicationMapper;
        this.gateway = remsApiQueryGateway;
    }

    public RetrievedApplication retrieveApplication(Long applicationId, String userId) {
        Application application = gateway.retrieveApplication(applicationId, userId);

        return applicationMapper.from(application);
    }
}

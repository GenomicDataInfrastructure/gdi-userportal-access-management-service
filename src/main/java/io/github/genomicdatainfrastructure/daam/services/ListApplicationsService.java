// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApplicationMapper;
import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.ApplicationOverview;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.V2Resource;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListApplicationsService {

    private RemsApplicationMapper applicationMapper;
    private final RemsApiQueryGateway gateway;

    public ListApplicationsService(
            RemsApplicationMapper applicationMapper,
            RemsApiQueryGateway remsApiQueryGateway
    ) {
        this.applicationMapper = applicationMapper;
        this.gateway = remsApiQueryGateway;
    }

    public List<ListedApplication> listApplications(String userId) {
        return applicationMapper.from(gateway.listApplications(userId));
    }
}

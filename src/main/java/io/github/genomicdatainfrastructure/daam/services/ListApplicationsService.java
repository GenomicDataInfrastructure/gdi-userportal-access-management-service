// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.ApplicationOverview;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ListApplicationsService {

    private final RemsApiQueryGateway gateway;

    public ListApplicationsService(
            RemsApiQueryGateway remsApiQueryGateway
    ) {
        this.gateway = remsApiQueryGateway;
    }

    public List<ListedApplication> listApplications(String userId) {
        return gateway.listApplications(userId)
                .stream()
                .map(this::parse)
                .toList();
    }

    public ListedApplication parse(ApplicationOverview applicationOverview) {
        return ListedApplication.builder()
                .id(applicationOverview.getApplicationId())
                .title(applicationOverview.getApplicationExternalId())
                .currentState(applicationOverview.getApplicationState().value())
                .stateChangedAt(applicationOverview.getApplicationLastActivity())
                .build();
    }
}

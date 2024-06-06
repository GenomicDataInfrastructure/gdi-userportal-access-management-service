// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.RetrieveGrantedDatasetIdentifiers;
import io.github.genomicdatainfrastructure.daam.model.RetrieveGrantedDatasetIdentifiersEntitlementsInner;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Entitlement;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RetrieveGrantedDatasetIdentifiersService {

    private final RemsApiQueryGateway remsApiQueryGateway;

    public RetrieveGrantedDatasetIdentifiersService(RemsApiQueryGateway remsApiQueryGateway) {
        this.remsApiQueryGateway = remsApiQueryGateway;
    }

    public RetrieveGrantedDatasetIdentifiers retrieveGrantedDatasetIdentifiers(String userId) {

        List<Entitlement> entitlements = remsApiQueryGateway.retrieveGrantedDatasetIdentifiers(
                userId);

        List<RetrieveGrantedDatasetIdentifiersEntitlementsInner> formattedEntitlements = entitlements
                .stream()
                .map(entitlement -> new RetrieveGrantedDatasetIdentifiersEntitlementsInner(
                        entitlement.getResource(),
                        entitlement.getStart(),
                        entitlement.getEnd()
                ))
                .toList();

        return new RetrieveGrantedDatasetIdentifiers(formattedEntitlements);
    }
}
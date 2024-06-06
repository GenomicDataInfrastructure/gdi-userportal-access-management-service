// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.RetrieveGrantedDatasetIdentifiers;
import io.github.genomicdatainfrastructure.daam.services.RetrieveGrantedDatasetIdentifiersService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;

import lombok.RequiredArgsConstructor;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;

@RequiredArgsConstructor
public class EntitlementQueryApiImpl implements EntitlementQueryApi {

    private final SecurityIdentity identity;
    private final RetrieveGrantedDatasetIdentifiersService retrieveGrantedDatasetIdentifiersService;

    @Override
    public RetrieveGrantedDatasetIdentifiers retrieveGrantedDatasetIdentifiers() {
        return retrieveGrantedDatasetIdentifiersService.retrieveGrantedDatasetIdentifiers(userId());
    }

    private String userId() {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getClaim(USER_ID_CLAIM);
    }
}
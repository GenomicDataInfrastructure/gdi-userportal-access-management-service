// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.RetrieveGrantedDatasetIdentifiers;
import io.github.genomicdatainfrastructure.daam.services.RetrieveGrantedDatasetIdentifiersService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

import static io.github.genomicdatainfrastructure.daam.api.ApplicationQueryApiImpl.DEFAULT_USER_ID_CLAIM;

public class EntitlementQueryApiImpl implements EntitlementQueryApi {

    private final SecurityIdentity identity;
    private final RetrieveGrantedDatasetIdentifiersService retrieveGrantedDatasetIdentifiersService;
    private final Optional<String> userIdClaim;

    public EntitlementQueryApiImpl(
            SecurityIdentity identity,
            RetrieveGrantedDatasetIdentifiersService retrieveGrantedDatasetIdentifiersService,
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.user-id-claim") Optional<String> userIdClaim
    ) {
        this.identity = identity;
        this.retrieveGrantedDatasetIdentifiersService = retrieveGrantedDatasetIdentifiersService;
        this.userIdClaim = userIdClaim;
    }

    @Override
    public RetrieveGrantedDatasetIdentifiers retrieveGrantedDatasetIdentifiers() {
        return retrieveGrantedDatasetIdentifiersService.retrieveGrantedDatasetIdentifiers(userId());
    }

    private String userId() {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getClaim(userIdClaim.orElse(DEFAULT_USER_ID_CLAIM));
    }
}

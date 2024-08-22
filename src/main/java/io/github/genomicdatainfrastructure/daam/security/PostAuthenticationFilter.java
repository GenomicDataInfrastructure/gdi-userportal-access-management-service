// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.security;

import io.github.genomicdatainfrastructure.daam.services.CreateRemsUserService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class PostAuthenticationFilter implements ContainerRequestFilter {

    public static final String USER_NAME_CLAIM = "preferred_username";
    public static final String EMAIL_CLAIM = "email";

    private final SecurityIdentity identity;
    private final CreateRemsUserService createRemsUserService;
    private final String userIdClaim;

    @Inject
    public PostAuthenticationFilter(
            SecurityIdentity identity,
            CreateRemsUserService createRemsUserService,
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.user-id-claim", defaultValue = "elixir_id") String userIdClaim
    ) {
        this.identity = identity;
        this.createRemsUserService = createRemsUserService;
        this.userIdClaim = userIdClaim;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!identity.isAnonymous()) {
            var oidcPrincipal = (OidcJwtCallerPrincipal) identity.getPrincipal();

            createRemsUserService.createRemsUser(
                    oidcPrincipal.getClaim(userIdClaim),
                    oidcPrincipal.getClaim(USER_NAME_CLAIM),
                    oidcPrincipal.getClaim(EMAIL_CLAIM)
            );
        }
    }
}

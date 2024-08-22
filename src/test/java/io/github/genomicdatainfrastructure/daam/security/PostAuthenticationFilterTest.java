// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.security;

import io.github.genomicdatainfrastructure.daam.services.CreateRemsUserService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Optional.of;
import static org.mockito.Mockito.*;

@QuarkusTest
class PostAuthenticationFilterTest {

    @Inject
    private PostAuthenticationFilter underTest;

    @InjectMock
    private SecurityIdentity securityIdentity;

    @InjectMock
    private CreateRemsUserService createRemsUserService;

    @BeforeEach
    void setUp() {
        underTest = new PostAuthenticationFilter(securityIdentity, createRemsUserService, of(
                "sub"));
    }

    @Test
    void nothing_happens_if_anonymous_request() {
        when(securityIdentity.isAnonymous()).thenReturn(true);
        underTest.filter(null);
        verify(createRemsUserService, never()).createRemsUser(any(), any(), any());
    }

    @Test
    void createRemsUser_is_called_if_authenticated_request() {
        when(securityIdentity.isAnonymous()).thenReturn(false);

        var principalMock = mock(OidcJwtCallerPrincipal.class);

        when(principalMock.getClaim("sub")).thenReturn("dummy_id");
        when(principalMock.getClaim("preferred_username")).thenReturn("dummy_username");
        when(principalMock.getClaim("email")).thenReturn("dummy_email");
        when(securityIdentity.getPrincipal()).thenReturn(principalMock);

        underTest.filter(null);
        verify(createRemsUserService)
                .createRemsUser("dummy_id", "dummy_username", "dummy_email");
    }
}

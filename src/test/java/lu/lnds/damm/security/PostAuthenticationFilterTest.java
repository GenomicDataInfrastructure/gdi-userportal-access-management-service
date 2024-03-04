// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package lu.lnds.damm.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import lu.lnds.daam.security.PostAuthenticationFilter;
import lu.lnds.daam.services.CreateRemsUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class PostAuthenticationFilterTest {

  @Inject private PostAuthenticationFilter underTest;

  @InjectMock private SecurityIdentity securityIdentity;

  @InjectMock private CreateRemsUserService createRemsUserService;

  @BeforeEach
  private void setUp() {
    underTest = new PostAuthenticationFilter(securityIdentity, createRemsUserService);
  }

  @Test
  public void nothing_happens_if_anonymous_request() {
    when(securityIdentity.isAnonymous()).thenReturn(true);
    underTest.filter(null);
  }

  @Test
  public void createRemsUser_is_called_if_authenticated_request() {
    when(securityIdentity.isAnonymous()).thenReturn(false);
    var principalMock = mock(OidcJwtCallerPrincipal.class);
    when(principalMock.getClaim("sub")).thenReturn("dummy_id");
    when(principalMock.getClaim("preferred_username")).thenReturn("dummy_username");
    when(principalMock.getClaim("email")).thenReturn("dummy_email");
    when(securityIdentity.getPrincipal()).thenReturn(principalMock);

    underTest.filter(null);
    Mockito.verify(createRemsUserService)
        .createRemsUser("dummy_id", "dummy_username", "dummy_email");
  }
}

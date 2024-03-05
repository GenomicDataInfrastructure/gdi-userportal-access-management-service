// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;

import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;
import io.github.genomicdatainfrastructure.daam.services.ListApplicationsService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationQueryApiImpl implements ApplicationQueryApi {

  private final SecurityIdentity identity;
  private final ListApplicationsService listApplicationsService;

  @Override
  public List<ListedApplication> listApplicationsV1() {
    var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
    String userId = principal.getClaim(USER_ID_CLAIM);
    return listApplicationsService.listApplications(userId);
  }

  @Override
  public RetrievedApplication retrieveApplicationV1(String id) {
    throw new UnsupportedOperationException("Unimplemented method 'retrieveApplicationV1'");
  }

  @Override
  public File retrieveAttachmentFromApplicationV1(String id, String attachmentId) {
    throw new UnsupportedOperationException(
        "Unimplemented method 'retrieveAttachmentFromApplicationV1'"
    );
  }
}

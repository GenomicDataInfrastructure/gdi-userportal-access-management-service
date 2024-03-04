// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package lu.lnds.daam.api;

import static lu.lnds.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lu.lnds.daam.model.ListedApplication;
import lu.lnds.daam.model.RetrievedApplication;
import lu.lnds.daam.services.ListApplicationsService;

@Authenticated
@RequiredArgsConstructor
public class ApplicationQueryApiImpl implements ApplicationQueryApi {

  private final SecurityIdentity identity;
  private final ListApplicationsService listApplicationsService;

  @Override
  public List<ListedApplication> listApplicationsV1() {
    var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
    return listApplicationsService.listApplications(principal.getClaim(USER_ID_CLAIM));
  }

  @Override
  public RetrievedApplication retrieveApplicationV1(String id) {
    throw new UnsupportedOperationException("Unimplemented method 'retrieveApplicationV1'");
  }

  @Override
  public File retrieveAttachmentFromApplicationV1(String id, String attachmentId) {
    throw new UnsupportedOperationException(
        "Unimplemented method 'retrieveAttachmentFromApplicationV1'");
  }
}

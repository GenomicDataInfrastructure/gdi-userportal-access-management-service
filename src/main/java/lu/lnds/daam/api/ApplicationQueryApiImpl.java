// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package lu.lnds.daam.api;

import java.io.File;
import java.util.List;
import lu.lnds.daam.model.ListedApplication;
import lu.lnds.daam.model.RetrievedApplication;

/** Applications API. */
public class ApplicationQueryApiImpl implements ApplicationQueryApi {

  @Override
  public List<ListedApplication> listApplicationsV1() {
    return List.of();
  }

  @Override
  public RetrievedApplication retrieveApplicationV1(String id) {
    return RetrievedApplication.builder().id(id).build();
  }

  @Override
  public File retrieveAttachmentFromApplicationV1(String id, String attachmentId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Unimplemented method 'retrieveAttachmentFromApplicationV1'");
  }
}

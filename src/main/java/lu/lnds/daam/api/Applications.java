// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package lu.lnds.daam.api;

import java.util.List;
import lu.lnds.daam.beans.ListedApplication;
import lu.lnds.daam.beans.RetrievedApplication;

/** Applications API. */
public class Applications implements ApplicationsApi {

  @Override
  public List<ListedApplication> listApplicationsV1() {
    return List.of();
  }

  @Override
  public RetrievedApplication retrieveApplicationV1(String id) {
    return RetrievedApplication.builder().id(id).build();
  }
}

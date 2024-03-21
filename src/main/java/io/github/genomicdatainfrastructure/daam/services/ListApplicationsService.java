// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationsApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.ApplicationOverview;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ListApplicationsService {

  private final String remsApiKey;
  private final RemsApplicationsApi applicationsApi;

  @Inject
  public ListApplicationsService(
      @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
      @RestClient RemsApplicationsApi applicationsApi
  ) {
    this.remsApiKey = remsApiKey;
    this.applicationsApi = applicationsApi;
  }

  public List<ListedApplication> listApplications(String userId) {
    return applicationsApi.apiMyApplicationsGet(remsApiKey, userId, null).stream()
        .map(this::parse)
        .toList();
  }

  public ListedApplication parse(ApplicationOverview applicationOverview) {
    return ListedApplication.builder()
        .id(applicationOverview.getApplicationId().toString())
        .title(applicationOverview.getApplicationExternalId())
        .currentState(applicationOverview.getApplicationState().value())
        .stateChangedAt(applicationOverview.getApplicationLastActivity())
        .build();
  }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationQueryApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsCatalogueItemQueryApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application.ApplicationStateEnum;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.ApplicationOverview;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CatalogueItem;
import io.quarkiverse.openapi.generator.annotations.GeneratedParam;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class RemsApiQueryGateway {

    private static final Set<ApplicationStateEnum> EDITABLE_STATES = Set.of(
            ApplicationStateEnum.DRAFT,
            ApplicationStateEnum.RETURNED
    );

    private final String remsApiKey;
    private final RemsApplicationQueryApi applicationsApi;
    private final RemsCatalogueItemQueryApi catalogueItemApi;

    public RemsApiQueryGateway(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationQueryApi applicationsApi,
            @RestClient RemsCatalogueItemQueryApi catalogueItemApi
    ) {
        this.remsApiKey = remsApiKey;
        this.applicationsApi = applicationsApi;
        this.catalogueItemApi = catalogueItemApi;
    }

    public Application retrieveApplication(Long applicationId, String userId) {
        try {
            return applicationsApi.apiApplicationsApplicationIdGet(
                    remsApiKey, userId, applicationId
            );
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ApplicationNotFoundException(applicationId);
            }
            throw e;
        }
    }

    public List<ApplicationOverview> listApplications(String userId) {
        return applicationsApi.apiMyApplicationsGet(remsApiKey, userId, null);
    }

    public void checkIfApplicationIsEditableByUser(Long id, String userId) {
        var application = retrieveApplication(id, userId);

        if (!application.getApplicationApplicant().getUserid().equals(userId)) {
            throw new UserNotApplicantException(id, userId);
        }

        if (!EDITABLE_STATES.contains(application.getApplicationState())) {
            throw new ApplicationNotInCorrectStateException(
                    id,
                    application.getApplicationState().value()
            );
        }
    }

    public CatalogueItem retrieveCatalogueItemByResourceId(String userId, String resourceId) {
        return catalogueItemApi.apiCatalogueItemsGet(remsApiKey, userId, resourceId).getFirst();
    }
}

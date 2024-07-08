// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotCreatedException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CatalogueItem;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationError;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateApplicationServiceTest {

    private static final String REMS_API_KEY = "remsApiKey";
    private CreateApplicationService underTest;
    private RemsApiQueryGateway gateway;
    private RemsApplicationCommandApi remsApplicationCommandApi;

    @BeforeEach
    void setUp() {
        gateway = mock(RemsApiQueryGateway.class);
        remsApplicationCommandApi = mock(RemsApplicationCommandApi.class);
        underTest = new CreateApplicationService(
                REMS_API_KEY,
                remsApplicationCommandApi,
                gateway
        );
    }

    @Test
    void cannot_create_for_unbundleable_catalogue_items() {
        var userId = "userId";
        var datasetId = "datasetId";
        var catalogueItemId = 11L;
        var command = CreateApplicationCommand.builder()
                .catalogueItemIds(List.of(catalogueItemId))
                .build();

        when(gateway.retrieveCatalogueItemByResourceId(userId, datasetId))
                .thenReturn(CatalogueItem.builder()
                        .id(catalogueItemId)
                        .build());

        when(remsApplicationCommandApi.apiApplicationsCreatePost(REMS_API_KEY, userId, command))
                .thenReturn(CreateApplicationResponse.builder()
                        .success(false)
                        .errors(List.of(
                                CreateApplicationError.builder()
                                        .type("unbundlable-catalogue-items")
                                        .catalogueItemIds(List.of(catalogueItemId))
                                        .build(),
                                CreateApplicationError.builder()
                                        .type("dummy")
                                        .build()
                        ))
                        .build());

        assertThatThrownBy(() -> underTest.createRemsApplication(CreateApplication.builder()
                .datasetIds(List.of(datasetId))
                .build(), userId))
                .isInstanceOf(ApplicationNotCreatedException.class)
                .hasMessage("Unbundleable catalogue items: 11; dummy");
    }
}

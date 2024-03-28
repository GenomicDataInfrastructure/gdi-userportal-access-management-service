// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.CatalogueItemNotFoundException;
import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CatalogueItem;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationCommand;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@ApplicationScoped
public class CreateApplicationService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;

    private final RemsApiQueryGateway gateway;

    @Inject
    public CreateApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi,
            RemsApiQueryGateway apiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
        this.gateway = apiQueryGateway;
    }

    public void createRemsApplication(CreateApplication createApplication, String userId) {
        var catalogueItemIds = getCatalogueItemIds(createApplication.getDatasetIds(), userId);
        var command = CreateApplicationCommand.builder()
                .catalogueItemIds(catalogueItemIds)
                .build();

        remsApplicationCommandApi.apiApplicationsCreatePost(remsApiKey, userId, command);
    }

    private List<Long> getCatalogueItemIds(List<String> datasetIds, String userId) {
        List<CompletableFuture<Long>> futureList = datasetIds.stream()
                .map(datasetId -> CompletableFuture.supplyAsync(() -> {
                    CatalogueItem item = gateway.retrieveCatalogueItemByResourceId(userId,
                            datasetId);
                    if (item == null) {
                        throw new CatalogueItemNotFoundException(datasetId);
                    }
                    return item.getCatalogueItemId();
                }))
                .toList();

        return futureList.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Thread.currentThread().interrupt();

                        if (e.getCause() instanceof CatalogueItemNotFoundException) {
                            throw (CatalogueItemNotFoundException) e.getCause();
                        }

                        throw new RuntimeException("Failed to retrieve catalogue item IDs", e);
                    }
                })
                .toList();
    }
}

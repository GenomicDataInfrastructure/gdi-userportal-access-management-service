// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotCreatedException;
import io.github.genomicdatainfrastructure.daam.exceptions.CatalogueItemNotFoundException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.CreateApplicationError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@ApplicationScoped
public class CreateApplicationService {

    private static final String UNBUNDLEABLE_CATALOGUE_ITEMS = "Unbundleable catalogue items: %s";

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

    public Long createRemsApplication(CreateApplication createApplication, String userId) {
        var catalogueItemIds = getCatalogueItemIds(createApplication.getDatasetIds(), userId);
        var command = CreateApplicationCommand.builder()
                .catalogueItemIds(catalogueItemIds)
                .build();

        var response = remsApplicationCommandApi.apiApplicationsCreatePost(
                remsApiKey, userId, command
        );

        if (Boolean.FALSE.equals(response.getSuccess())) {
            var message = parseErrorsToMessage(response.getErrors());
            throw new ApplicationNotCreatedException(message);
        }

        return response.getApplicationId();
    }

    private String parseErrorsToMessage(List<CreateApplicationError> errors) {
        return errors.stream().map(
                error -> switch (error.getType()) {
                    case "unbundlable-catalogue-items" -> UNBUNDLEABLE_CATALOGUE_ITEMS
                            .formatted(error.getCatalogueItemIds().stream()
                                    .map(Objects::toString)
                                    .collect(Collectors.joining(", ")));
                    default -> error.getType();
                }
        ).collect(joining("; "));
    }

    private List<Long> getCatalogueItemIds(List<String> datasetIds, String userId) {
        var futures = datasetIds.stream()
                .map(datasetId -> fetchCatalogueItemIdAsync(userId, datasetId))
                .toList();

        try {
            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        } catch (CompletionException e) {
            if (e.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e.getCause());
        }
    }

    private CompletableFuture<Long> fetchCatalogueItemIdAsync(String userId, String datasetId) {
        return CompletableFuture.supplyAsync(() -> {
            var item = gateway.retrieveCatalogueItemByResourceId(userId, datasetId);
            if (item == null) {
                throw new CatalogueItemNotFoundException(datasetId);
            }
            return item.getId();
        });
    }
}

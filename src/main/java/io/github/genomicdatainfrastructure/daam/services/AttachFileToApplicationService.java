// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import java.io.File;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.AddedAttachment;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi.ApiApplicationsAddAttachmentPostMultipartForm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AttachFileToApplicationService {

    private final String remsApiKey;
    private final RemsApplicationCommandApi remsApplicationCommandApi;
    private final RemsApiQueryGateway gateway;

    @Inject
    public AttachFileToApplicationService(
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.api-key") String remsApiKey,
            @RestClient RemsApplicationCommandApi remsApplicationCommandApi,
            RemsApiQueryGateway apiQueryGateway
    ) {
        this.remsApiKey = remsApiKey;
        this.remsApplicationCommandApi = remsApplicationCommandApi;
        this.gateway = apiQueryGateway;
    }

    public AddedAttachment attach(Long applicationId, File file, String userId) {
        gateway.checkIfApplicationIsEditableByUser(applicationId, userId);

        var multipartForm = new ApiApplicationsAddAttachmentPostMultipartForm();
        multipartForm._file = file;

        var attachmentResponse = remsApplicationCommandApi.apiApplicationsAddAttachmentPost(
                multipartForm, applicationId, remsApiKey, userId
        );

        return AddedAttachment.builder()
                .id(attachmentResponse.getId())
                .build();
    }
}

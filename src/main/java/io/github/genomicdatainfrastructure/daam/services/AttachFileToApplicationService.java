// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

import java.io.File;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentFormatNotAcceptedException;
import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentIsNullException;
import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentTooLargeException;
import io.github.genomicdatainfrastructure.daam.gateways.RemsApiQueryGateway;
import io.github.genomicdatainfrastructure.daam.model.AddedAttachment;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationCommandApi.ApiApplicationsAddAttachmentPostMultipartForm;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.AttachFileResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;
import lombok.SneakyThrows;

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

    public AddedAttachment attach(Long applicationId, String userId, String filename, File file) {
        gateway.checkIfApplicationIsEditableByUser(applicationId, userId);

        var attachmentResponse = submitFileToRems(applicationId, userId, filename, file);

        return AddedAttachment.builder()
                .id(attachmentResponse.getId())
                .build();
    }

    @SneakyThrows
    private File tempFile(String filename, File file) {
        var tempFile = createTempFile(null, filename);
        var content = readAllBytes(file.toPath());
        write(tempFile, content);
        return tempFile.toFile();
    }

    private AttachFileResponse submitFileToRems(
            Long applicationId,
            String userId,
            String filename,
            File file
    ) {
        if (filename == null || file == null) {
            throw new AttachmentIsNullException();
        }

        var multipartForm = new ApiApplicationsAddAttachmentPostMultipartForm();
        multipartForm._file = tempFile(filename, file);
        try {
            return remsApplicationCommandApi.apiApplicationsAddAttachmentPost(
                    multipartForm, applicationId, remsApiKey, userId
            );
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode()) {
                throw new AttachmentTooLargeException(multipartForm._file.getName());
            }
            if (e.getResponse().getStatus() == Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode()) {
                throw new AttachmentFormatNotAcceptedException(multipartForm._file.getName());
            }
            throw e;
        }
    }
}

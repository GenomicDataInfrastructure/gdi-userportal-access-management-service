// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;
import java.io.File;
import java.util.List;

import io.github.genomicdatainfrastructure.daam.services.CreateApplicationsService;
import io.github.genomicdatainfrastructure.daam.model.AddApplicationEvent;
import io.github.genomicdatainfrastructure.daam.model.AddedAttachments;
import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.github.genomicdatainfrastructure.daam.model.RemoveMember;
import io.github.genomicdatainfrastructure.daam.model.SaveFormsAndDuos;
import io.github.genomicdatainfrastructure.daam.model.UpdateDatasets;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationCommandApiImpl implements ApplicationCommandApi {

    private final CreateApplicationsService createApplicationsService;

    @Override
    public Response acceptApplicationTermsV1(String id) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'acceptApplicationTermsV1'"
        );
    }

    @Override
    public List<AddedAttachments> addAttachmentToApplicationV1(String id, File body) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'addAttachmentToApplicationV1'"
        );
    }

    @Override
    public Response addEventToApplicationV1(String id, AddApplicationEvent addApplicationEvent) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'addEventToApplicationV1'"
        );
    }

    @Override
    public Response cancelApplicationV1(String id) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'cancelApplicationV1'"
        );
    }

    @Override
    public Response copyApplicationAsNewV1(String id) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'copyApplicationAsNewV1'"
        );
    }

    @Override
    public Response createApplicationV1(CreateApplication createApplication) {
        createApplicationsService.createRemsApplication(createApplication);
        return Response.noContent().build();
    }

    @Override
    public Response inviteMemberToApplicationV1(String id) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'inviteMemberToApplicationV1'"
        );
    }

    @Override
    public Response removeMemberFromApplicationV1(String id, RemoveMember removeMember) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'removeMemberFromApplicationV1'"
        );
    }

    @Override
    public Response saveApplicationFormsAndDuosV1(String id, SaveFormsAndDuos saveFormsAndDuos) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'saveApplicationFormsAndDuosV1'"
        );
    }

    @Override
    public Response submitApplicationV1(String id) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'submitApplicationV1'"
        );
    }

    @Override
    public Response updateDatasetsOfApplicationV1(String id, List<UpdateDatasets> updateDatasets) {
        throw new UnsupportedOperationException(
            "Unimplemented method 'updateDatasetsOfApplicationV1'"
        );
    }
}
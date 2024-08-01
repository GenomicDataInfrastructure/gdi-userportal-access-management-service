// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.*;
import io.github.genomicdatainfrastructure.daam.services.*;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;

@RequiredArgsConstructor
public class ApplicationCommandApiImpl implements ApplicationCommandApi {

    private final SecurityIdentity identity;
    private final SaveApplicationService saveApplicationService;
    private final CreateApplicationService createApplicationService;
    private final SubmitApplicationService submitApplicationService;
    private final AttachFileToApplicationService attachFileToApplicationService;
    private final AcceptTermsService acceptTermsService;

    @Override
    public Response addEventToApplicationV1(Long id, AddApplicationEvent addApplicationEvent) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'addEventToApplicationV1'"
        );
    }

    @Override
    public Response cancelApplicationV1(Long id) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'cancelApplicationV1'"
        );
    }

    @Override
    public Response copyApplicationAsNewV1(Long id) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'copyApplicationAsNewV1'"
        );
    }

    @Override
    public CreateApplicationResponse createApplicationV1(CreateApplication createApplication) {
        var applicationId = createApplicationService.createRemsApplication(createApplication,
                userId());

        return new CreateApplicationResponse().applicationId(applicationId);
    }

    @Override
    public Response inviteMemberToApplicationV1(Long id) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'inviteMemberToApplicationV1'"
        );
    }

    @Override
    public Response removeMemberFromApplicationV1(Long id, RemoveMember removeMember) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'removeMemberFromApplicationV1'"
        );
    }

    @Override
    public Response saveApplicationFormsAndDuosV1(Long id, SaveFormsAndDuos saveFormsAndDuos) {
        saveApplicationService.save(userId(), id, saveFormsAndDuos);
        return Response
                .noContent()
                .build();
    }

    @Override
    public Response submitApplicationV1(Long id) {
        submitApplicationService.submitApplication(id, userId());
        return Response
                .noContent()
                .build();
    }

    @Override
    public Response updateDatasetsOfApplicationV1(Long id, List<UpdateDatasets> updateDatasets) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'updateDatasetsOfApplicationV1'"
        );
    }

    @Override
    public AddedAttachment addAttachmentToApplicationV1(
            AddAttachmentToApplicationV1MultipartForm multipartForm,
            Long id
    ) {
        var file = multipartForm._file;
        return attachFileToApplicationService.attach(
                id, userId(), file.fileName(), file.filePath().toFile()
        );
    }

    private String userId() {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getClaim(USER_ID_CLAIM);
    }

    @Override
    public Response acceptApplicationTermsV1(Long id, AcceptTermsCommand acceptTermsCommand) {
        String userId = userId();

        io.github.genomicdatainfrastructure.daam.remote.rems.model.AcceptLicensesCommand remoteAcceptLicensesCommand = new io.github.genomicdatainfrastructure.daam.remote.rems.model.AcceptLicensesCommand();
        remoteAcceptLicensesCommand.setApplicationId(acceptTermsCommand.getApplicationId());
        remoteAcceptLicensesCommand.setAcceptedLicenses(acceptTermsCommand.getAcceptedLicenses());

        acceptTermsService.acceptTerms(id, userId, remoteAcceptLicensesCommand);
        return Response.noContent().build();
    }
}

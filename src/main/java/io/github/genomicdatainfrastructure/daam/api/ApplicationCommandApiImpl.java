// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.*;
import io.github.genomicdatainfrastructure.daam.services.*;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Optional;

import static io.github.genomicdatainfrastructure.daam.api.ApplicationQueryApiImpl.DEFAULT_USER_ID_CLAIM;

public class ApplicationCommandApiImpl implements ApplicationCommandApi {

    private final SecurityIdentity identity;
    private final SaveApplicationService saveApplicationService;
    private final CreateApplicationService createApplicationService;
    private final SubmitApplicationService submitApplicationService;
    private final DeleteApplicationService deleteApplicationService;
    private final AttachFileToApplicationService attachFileToApplicationService;
    private final AcceptTermsService acceptTermsService;
    private final InviteMemberService inviteMemberService;
    private final Optional<String> userIdClaim;

    public ApplicationCommandApiImpl(
            SecurityIdentity identity,
            SaveApplicationService saveApplicationService,
            CreateApplicationService createApplicationService,
            SubmitApplicationService submitApplicationService,
            DeleteApplicationService deleteApplicationService,
            AttachFileToApplicationService attachFileToApplicationService,
            AcceptTermsService acceptTermsService,
            InviteMemberService inviteMemberService,
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.user-id-claim") Optional<String> userIdClaim
    ) {
        this.identity = identity;
        this.saveApplicationService = saveApplicationService;
        this.createApplicationService = createApplicationService;
        this.submitApplicationService = submitApplicationService;
        this.deleteApplicationService = deleteApplicationService;
        this.attachFileToApplicationService = attachFileToApplicationService;
        this.acceptTermsService = acceptTermsService;
        this.inviteMemberService = inviteMemberService;
        this.userIdClaim = userIdClaim;
    }

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

    @java.lang.Override
    public Response deleteApplicationV1(Long id) {
        deleteApplicationService.deleteApplication(id, userId());
        return Response.noContent().build();
    }

    @Override
    public Response inviteMemberToApplicationV1(Long applicationId, InviteMember inviteMember) {
        inviteMemberService.invite(applicationId, userId(), inviteMember);
        return Response.noContent().build();
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

    @Override
    public Response acceptApplicationTermsV1(Long id, AcceptTermsCommand acceptTermsCommand) {
        String userId = userId();
        acceptTermsService.acceptTerms(id, userId, acceptTermsCommand);
        return Response.noContent().build();
    }

    private String userId() {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getClaim(userIdClaim.orElse(DEFAULT_USER_ID_CLAIM));
    }
}

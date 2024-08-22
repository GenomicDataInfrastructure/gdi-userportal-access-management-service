// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;
import io.github.genomicdatainfrastructure.daam.services.ListApplicationsService;
import io.github.genomicdatainfrastructure.daam.services.RetrieveApplicationService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

public class ApplicationQueryApiImpl implements ApplicationQueryApi {

    private final SecurityIdentity identity;
    private final ListApplicationsService listApplicationsService;
    private final RetrieveApplicationService retrieveApplicationService;
    private final String userIdClaim;

    public ApplicationQueryApiImpl(
            SecurityIdentity identity,
            ListApplicationsService listApplicationsService,
            RetrieveApplicationService retrieveApplicationService,
            @ConfigProperty(name = "quarkus.rest-client.rems_yaml.user-id-claim") String userIdClaim
    ) {
        this.identity = identity;
        this.listApplicationsService = listApplicationsService;
        this.retrieveApplicationService = retrieveApplicationService;
        this.userIdClaim = userIdClaim;
    }

    @Override
    public List<ListedApplication> listApplicationsV1() {
        return listApplicationsService.listApplications(userId());
    }

    @Override
    public RetrievedApplication retrieveApplicationV1(Long id) {
        return retrieveApplicationService.retrieveApplication(id, userId());
    }

    @Override
    public FileUpload retrieveAttachmentFromApplicationV1(Long id, Long attachmentId) {
        throw new UnsupportedOperationException(
                "Unimplemented method 'retrieveAttachmentFromApplicationV1'"
        );
    }

    private String userId() {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getClaim(userIdClaim);
    }
}

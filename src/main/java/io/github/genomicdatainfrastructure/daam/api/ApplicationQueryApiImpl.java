// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.ListedApplication;
import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;
import io.github.genomicdatainfrastructure.daam.model.RetrieveGrantedDatasetIdentifiers;
import io.github.genomicdatainfrastructure.daam.services.ListApplicationsService;
import io.github.genomicdatainfrastructure.daam.services.RetrieveApplicationService;
import io.github.genomicdatainfrastructure.daam.services.RetrieveGrantedDatasetIdentifiersService;
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

import static io.github.genomicdatainfrastructure.daam.security.PostAuthenticationFilter.USER_ID_CLAIM;

@RequiredArgsConstructor
public class ApplicationQueryApiImpl implements ApplicationQueryApi {

    private final SecurityIdentity identity;
    private final ListApplicationsService listApplicationsService;
    private final RetrieveApplicationService retrieveApplicationService;
    private final RetrieveGrantedDatasetIdentifiersService retrieveGrantedDatasetIdentifiersService;

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

    @Override
    public RetrieveGrantedDatasetIdentifiers retrieveGrantedDatasetIdentifiers() {
        return retrieveGrantedDatasetIdentifiersService.retrieveGrantedDatasetIdentifiers(userId());
    }

    private String userId() {
        var principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
        return principal.getClaim(USER_ID_CLAIM);
    }
}

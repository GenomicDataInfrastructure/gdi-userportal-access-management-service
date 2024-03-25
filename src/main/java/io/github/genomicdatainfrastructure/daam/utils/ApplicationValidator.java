// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.utils;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.remote.rems.api.RemsApplicationsApi;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.Application;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class ApplicationValidator {

    public Application validate(Long applicationId, String remsApiKey, String userId, RemsApplicationsApi applicationsApi) {
        try {
            return applicationsApi.apiApplicationsApplicationIdGet(
                    applicationId, remsApiKey, userId);
        } catch (WebApplicationException e) {
            if (e.getResponse().getStatus() == 404) {
                throw new ApplicationNotFoundException(applicationId);
            }
            throw e;
        }
    }
}

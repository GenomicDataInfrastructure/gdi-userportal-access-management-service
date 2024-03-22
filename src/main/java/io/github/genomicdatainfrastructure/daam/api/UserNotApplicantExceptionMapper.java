// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserNotApplicantExceptionMapper implements ExceptionMapper<UserNotApplicantException> {
    @Override
    public Response toResponse(UserNotApplicantException exception) {

        ErrorResponse errorResponse = new ErrorResponse(
                "User Not Applicant",
                Response.Status.FORBIDDEN.getStatusCode(),
                exception.getMessage()
        );

        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

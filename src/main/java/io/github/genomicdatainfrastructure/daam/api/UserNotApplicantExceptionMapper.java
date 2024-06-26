// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import java.util.List;

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
        var errorResponse = new ErrorResponse(
                "User Not Applicant",
                FORBIDDEN.getStatusCode(),
                exception.getMessage(),
                List.of()
        );

        return Response
                .status(FORBIDDEN)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

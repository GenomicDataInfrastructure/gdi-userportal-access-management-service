// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import java.util.Collections;

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
                Collections.singletonList(
                        "The current user is not the applicant of the specified application and therefore does not have the necessary permissions to perform this action.")
        );

        return Response
                .status(FORBIDDEN)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

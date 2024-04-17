// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationSubmissionException;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import java.util.List;

import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationSubmissionExceptionMapper implements
        ExceptionMapper<ApplicationSubmissionException> {

    @Override
    public Response toResponse(ApplicationSubmissionException exception) {
        var errorResponse = new ErrorResponse(
                "Application could not be submitted",
                BAD_REQUEST.getStatusCode(),
                exception.getMessage(),
                List.of("The application submission failed. Ensure all required fields are filled out correctly.")
        );

        return Response
                .status(BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
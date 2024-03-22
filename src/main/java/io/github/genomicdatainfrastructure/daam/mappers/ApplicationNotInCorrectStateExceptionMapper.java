// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.mappers;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationNotInCorrectStateExceptionMapper implements ExceptionMapper<ApplicationNotInCorrectStateException> {
    @Override
    public Response toResponse(ApplicationNotInCorrectStateException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Application Not In Correct State",
                Response.Status.PRECONDITION_REQUIRED.getStatusCode(),
                exception.getMessage()
        );

        return Response
                .status(Response.Status.PRECONDITION_REQUIRED)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

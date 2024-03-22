// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationNotFoundExceptionMapper implements
        ExceptionMapper<ApplicationNotFoundException> {

    @Override
    public Response toResponse(ApplicationNotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Application Not Found",
                Response.Status.NOT_FOUND.getStatusCode(),
                exception.getMessage()
        );

        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

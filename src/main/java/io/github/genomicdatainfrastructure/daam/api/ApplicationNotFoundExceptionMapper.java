// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class ApplicationNotFoundExceptionMapper implements
        ExceptionMapper<ApplicationNotFoundException> {

    @Override
    public Response toResponse(ApplicationNotFoundException exception) {
        var errorResponse = new ErrorResponse(
                "Application Not Found",
                NOT_FOUND.getStatusCode(),
                exception.getMessage(),
                List.of()
        );

        return Response
                .status(NOT_FOUND)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotCreatedException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class ApplicationNotCreatedExceptionMapper implements
        ExceptionMapper<ApplicationNotCreatedException> {

    @Override
    public Response toResponse(ApplicationNotCreatedException exception) {
        var errorResponse = new ErrorResponse(
                "Application Not Created",
                BAD_REQUEST.getStatusCode(),
                exception.getMessage(),
                List.of()
        );

        return Response
                .status(BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

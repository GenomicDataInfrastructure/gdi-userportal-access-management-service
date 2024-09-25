// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import java.util.List;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationNotInCorrectStateExceptionMapper implements
        ExceptionMapper<ApplicationNotInCorrectStateException> {

    @Override
    public Response toResponse(ApplicationNotInCorrectStateException exception) {
        var errorResponse = new ErrorResponse(
                "Application Not In Correct State",
                CONFLICT.getStatusCode(),
                exception.getMessage(),
                List.of()
        );

        return Response
                .status(CONFLICT)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

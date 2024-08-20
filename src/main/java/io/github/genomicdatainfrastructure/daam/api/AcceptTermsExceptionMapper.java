// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.exceptions.AcceptTermsException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

@Provider
public class AcceptTermsExceptionMapper implements ExceptionMapper<AcceptTermsException> {

    @Override
    public Response toResponse(AcceptTermsException exception) {
        var errorResponse = new ErrorResponse(
                "Terms and conditions could not be accepted",
                BAD_REQUEST.getStatusCode(),
                exception.getMessage(),
                exception.getWarnings()
        );

        return Response
                .status(BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.mappers;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationNotInCorrectStateExceptionMapper implements ExceptionMapper<ApplicationNotInCorrectStateException> {
    @Override
    public Response toResponse(ApplicationNotInCorrectStateException exception) {
        return Response.status(Response.Status.PRECONDITION_REQUIRED).entity(exception.getMessage()).build();
    }
}

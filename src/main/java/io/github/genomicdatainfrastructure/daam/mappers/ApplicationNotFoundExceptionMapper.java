// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.mappers;
import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotFoundException;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

@Provider
public class ApplicationNotFoundExceptionMapper implements ExceptionMapper<ApplicationNotFoundException> {
    @Override
    public Response toResponse(ApplicationNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.List;

import io.github.genomicdatainfrastructure.daam.exceptions.CatalogueItemNotFoundException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatalogueItemNotFoundExceptionMapper implements
        ExceptionMapper<CatalogueItemNotFoundException> {

    @Override
    public Response toResponse(CatalogueItemNotFoundException exception) {
        var errorResponse = new ErrorResponse(
                "REMS Catalogue Item Not Found",
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

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import io.github.genomicdatainfrastructure.daam.exceptions.AttachmentFormatNotAcceptedException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class AttachmentFormatNotAcceptedExceptionMapper implements
        ExceptionMapper<AttachmentFormatNotAcceptedException> {

    @Override
    public Response toResponse(AttachmentFormatNotAcceptedException exception) {
        var errorResponse = new ErrorResponse(
                "Attachment Format Not Accepted",
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

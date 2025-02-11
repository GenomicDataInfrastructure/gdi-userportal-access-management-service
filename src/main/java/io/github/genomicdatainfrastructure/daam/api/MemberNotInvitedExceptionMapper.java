// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.exceptions.MemberNotInvitedException;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Provider
public class MemberNotInvitedExceptionMapper implements ExceptionMapper<MemberNotInvitedException> {

    @Override
    public Response toResponse(MemberNotInvitedException exception) {
        var errorResponse = new ErrorResponse(
                exception.getMessage(),
                INTERNAL_SERVER_ERROR.getStatusCode(),
                "",
                List.of()
        );

        return Response
                .status(INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

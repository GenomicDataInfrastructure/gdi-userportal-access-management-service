// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.mappers;

import io.github.genomicdatainfrastructure.daam.exceptions.UserNotApplicantException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserNotApplicantExceptionMapper implements ExceptionMapper<UserNotApplicantException> {
    @Override
    public Response toResponse(UserNotApplicantException exception) {
        return Response.status(Response.Status.FORBIDDEN).entity(exception.getMessage()).build();
    }
}

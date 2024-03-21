package io.github.genomicdatainfrastructure.daam.mappers;

import io.github.genomicdatainfrastructure.daam.exceptions.ApplicationNotInCorrectStateException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApplicationNotInCorrectStateExceptionMapper implements ExceptionMapper<ApplicationNotInCorrectStateException> {
    @Override
    public Response toResponse(ApplicationNotInCorrectStateException exception) {
        return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
}

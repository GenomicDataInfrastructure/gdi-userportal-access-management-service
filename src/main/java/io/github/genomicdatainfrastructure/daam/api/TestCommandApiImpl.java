package io.github.genomicdatainfrastructure.daam.api;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

public class TestCommandApiImpl implements TestCommandApi {

    @Override
    public Response testV1(String test) {
        var errorResponse = new ErrorResponse(
                "Test",
                BAD_REQUEST.getStatusCode(),
                test,
                List.of()
        );

        return Response
                .status(BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

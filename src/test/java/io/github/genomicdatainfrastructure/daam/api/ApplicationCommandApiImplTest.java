// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.github.genomicdatainfrastructure.daam.services.CreateApplicationsService;
import io.quarkus.test.junit.QuarkusMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@QuarkusTest
public class ApplicationCommandApiImplTest {

    private CreateApplicationsService mockService;

    @BeforeEach
    public void setup() {
        mockService = Mockito.mock(CreateApplicationsService.class);
        QuarkusMock.installMockForType(mockService, CreateApplicationsService.class);
    }

        @Test
    void createApplication_when_validRequest() {
        String requestBody = """
            {
                "datasetIds": ["123", "456"]
            }
            """;

        given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/v1/applications/create-application")
            .then()
            .statusCode(204);

        verify(mockService, Mockito.times(1)).createRemsApplication(any(CreateApplication.class));
    }
}

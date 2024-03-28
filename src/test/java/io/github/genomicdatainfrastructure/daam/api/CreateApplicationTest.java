// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class CreateApplicationTest extends BaseTest {

    @Test
    void can_create_application() {
        var createApplication = CreateApplication.builder()
                .datasetIds(List.of("12345"))
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(createApplication)
                .when()
                .post("/api/v1/applications/create")
                .then()
                .statusCode(204);
    }

    @Test
    void application_creation_fails_for_invalid_dataset_id() {
        var createApplication = CreateApplication.builder()
                .datasetIds(List.of("invalid"))
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(createApplication)
                .when()
                .post("/api/v1/applications/create")
                .then()
                .statusCode(404);
    }

    @Test
    void cannot_create_application_when_anonymous_request() {
        var createApplication = CreateApplication.builder()
                .datasetIds(List.of("dataset1", "dataset2", "dataset3"))
                .build();

        given()
                .contentType("application/json")
                .body(createApplication)
                .when()
                .get("/api/v1/applications/create")
                .then()
                .statusCode(401);
    }
}

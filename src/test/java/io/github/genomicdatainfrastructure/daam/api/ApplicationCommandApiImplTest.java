// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;


@QuarkusTest
public class ApplicationCommandApiImplTest {

    private final KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    void unauthorized_when_no_user() {
        given().when().get("/api/v1/applications/create").then().statusCode(401);
    }


    @Test
    void createApplication_when_authenticated() {
        CreateApplication createApplication = CreateApplication.builder()
                .datasetIds(Arrays.asList("dataset1", "dataset2", "dataset3"))
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
    void submitApplication_when_authenticated() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .post("/api/v1/applications/1/submit")
                .then()
                .statusCode(204);
    }

    @Test
    void submitApplication_when_application_not_found() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .post("/api/v1/applications/12345/submit")
                .then()
                .statusCode(404);
    }

    @Test
    void submitApplication_when_not_applicant() {
        given()
                .auth()
                .oauth2(getAccessToken("jdoe"))
                .when()
                .post("/api/v1/applications/1/submit")
                .then()
                .statusCode(403);
    }

    @Test
    void submitApplication_when_application_not_in_submittable_state() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .post("/api/v1/applications/2/submit")
                .then()
                .statusCode(428);
    }


    private String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
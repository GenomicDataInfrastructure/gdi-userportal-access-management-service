// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;
import io.github.genomicdatainfrastructure.daam.model.SaveFormsAndDuos;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class SaveApplicationTest extends BaseTest {

    @Test
    void can_save_application() {
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(saveFormsAndDuos)
                .when()
                .post("/api/v1/applications/1/save-forms-and-duos")
                .then()
                .statusCode(204);
    }

    @Test
    void cannot_save_application_when_application_not_found() {
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(saveFormsAndDuos)
                .when()
                .post("/api/v1/applications/12345/save-forms-and-duos")
                .then()
                .statusCode(404);
    }

    @Test
    void cannot_save_application_when_not_same_applicant() {
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .build();
        given()
                .auth()
                .oauth2(getAccessToken("jdoe"))
                .contentType("application/json")
                .body(saveFormsAndDuos)
                .when()
                .post("/api/v1/applications/1/save-forms-and-duos")
                .then()
                .statusCode(403);
    }

    @Test
    void cannot_save_application_when_not_in_submittable_state() {
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(saveFormsAndDuos)
                .when()
                .post("/api/v1/applications/2/save-forms-and-duos")
                .then()
                .statusCode(409);
    }

    @Test
    void cannot_save_application_when_anonymous_request() {
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .build();

        given()
                .contentType("application/json")
                .body(saveFormsAndDuos)
                .when()
                .post("/api/v1/applications/1/save-forms-and-duos")
                .then()
                .statusCode(401);
    }
}

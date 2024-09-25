// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class DeleteApplicationTest extends BaseTest {

    @Test
    void can_delete_draft_application() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .delete("/api/v1/applications/1")
                .then()
                .statusCode(204);
    }

    @Test
    void cannot_delete_application_when_not_found() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .delete("/api/v1/applications/12345")
                .then()
                .statusCode(404);
    }

    @Test
    void cannot_delete_application_when_not_same_applicant() {
        given()
                .auth()
                .oauth2(getAccessToken("jdoe"))
                .when()
                .delete("/api/v1/applications/1")
                .then()
                .statusCode(403);
    }

    @Test
    void cannot_delete_application_when_not_draft() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .delete("/api/v1/applications/2")
                .then()
                .statusCode(409);
    }

    @Test
    void cannot_delete_application_when_anonymous_request() {
        given()
                .when()
                .delete("/api/v1/applications/1")
                .then()
                .statusCode(401);
    }
}

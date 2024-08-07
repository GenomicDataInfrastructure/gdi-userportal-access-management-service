// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class AcceptTermsTest extends BaseTest {

    @Test
    void can_accept_terms() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body("{ \"accepted-licenses\": [1, 2] }")
                .when()
                .post("/api/v1/applications/1/accept-terms")
                .then()
                .statusCode(204);
    }

    @Test
    void cannot_accept_terms_when_application_not_found() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body("{ \"accepted-licenses\": [1, 2] }")
                .when()
                .post("/api/v1/applications/12345/accept-terms")
                .then()
                .statusCode(404)
                .body("title", equalTo("Application Not Found"));
    }

    @Test
    void cannot_accept_terms_when_application_not_for_applicant() {
        given()
                .auth()
                .oauth2(getAccessToken("jdoe"))
                .contentType("application/json")
                .body("{ \"accepted-licenses\": [1, 2] }")
                .when()
                .post("/api/v1/applications/1/accept-terms")
                .then()
                .statusCode(403)
                .body("title", equalTo("User Not Applicant"));
    }

    @Test
    void cannot_accept_terms_when_not_in_submittable_state() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body("{ \"accepted-licenses\": [1, 2] }")
                .when()
                .post("/api/v1/applications/2/accept-terms")
                .then()
                .statusCode(428)
                .body("title", equalTo("Application Not In Correct State"));
    }

    @Test
    void cannot_accept_terms_when_success_false() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body("{\"accepted-licenses\": [3, 4]}")
                .when()
                .post("/api/v1/applications/44/accept-terms")
                .then()
                .statusCode(400)
                .body("title", equalTo("Could not accept terms"));
    }

}

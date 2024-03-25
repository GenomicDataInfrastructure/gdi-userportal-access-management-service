// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ListApplicationsTest extends BaseTest {

    @Test
    void can_list_applications() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/applications")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo(25))
                .body("[0].title", equalTo("2024/14"))
                .body("[0].currentState", equalTo("application.state/draft"))
                .body("[0].stateChangedAt", equalTo("2024-03-05T19:44:46.208Z"));
    }

    @Test
    void cannot_list_applications_when_anonymous_request() {
        given()
                .when()
                .get("/api/v1/applications")
                .then()
                .statusCode(401);
    }
}

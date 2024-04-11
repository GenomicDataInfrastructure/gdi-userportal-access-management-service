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
                .body("[0].id", equalTo(28))
                .body("[0].title", equalTo("2024/11"))
                .body("[0].description", equalTo(""))
                .body("[0].currentState", equalTo("application.state/approved"))
                .body("[0].stateChangedAt", equalTo("2024-03-28T10:38:34.215Z"))
                .body("[0].createdAt", equalTo("2024-03-28T07:33:17.219Z"))
                .body("[0].datasets[0].id", equalTo(27))
                .body("[0].datasets[0].title[0].language", equalTo("en"))
                .body("[0].datasets[0].title[0].name", equalTo(
                        "Esimerkkihakemus: Yksittäinen rekisterinpitäjä"))
                .body("[0].datasets[0].externalId", equalTo("testi.fi"));
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

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class RetrieveGrantedDatasetIdentifiersTest extends BaseTest {

    @Test
    void canRetrieveGrantedDatasetIdentifiers() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/entitlements")
                .then()
                .statusCode(200)
                .body("entitlements[0].application-id", equalTo("1"))
                .body("entitlements[0].start", equalTo("2024-05-06T22:14:09.036Z"))
                .body("entitlements[0].end", equalTo("2024-05-06T22:14:09.036Z"));
    }

    @Test
    void cannotRetrieveGrantedDatasetIdentifiersWhenAnonymousRequest() {
        given()
                .when()
                .get("/api/v1/entitlements")
                .then()
                .statusCode(401);
    }
}
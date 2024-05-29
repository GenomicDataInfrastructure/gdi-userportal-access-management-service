// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class EntitlementsTest extends BaseTest {

    @Test
    void can_retrieve_granted_dataset_identifiers() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/entitlements")
                .then()
                .statusCode(200)
                .body("entitlements.size()", equalTo(1))
                .body("entitlements[0].id", equalTo("12345"))
                .body("entitlements[0].start", equalTo("2024-05-06T22:14:09.036Z"))
                .body("entitlements[0].end", equalTo("2024-05-06T22:14:09.036Z"));
    }

    @Test
    void cannot_retrieve_granted_dataset_identifiers_when_anonymous() {
        given()
                .when()
                .get("/api/v1/entitlements")
                .then()
                .statusCode(401);
    }

    @Test
    void cannot_retrieve_granted_dataset_identifiers_with_invalid_token() {
        given()
                .auth()
                .oauth2("invalid-token")
                .when()
                .get("/api/v1/entitlements")
                .then()
                .statusCode(401);
    }

    @Test
    void cannot_retrieve_granted_dataset_identifiers_when_user_has_no_entitlements() {
        given()
                .auth()
                .oauth2(getAccessToken("bob"))
                .when()
                .get("/api/v1/entitlements")
                .then()
                .statusCode(200)
                .body("entitlements.size()", equalTo(0));
    }
}

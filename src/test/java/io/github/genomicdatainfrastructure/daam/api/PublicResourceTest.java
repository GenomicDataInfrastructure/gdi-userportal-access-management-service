// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class PublicResourceTest {

    @ParameterizedTest
    @ValueSource(strings = {"/", "/q/swagger-ui", "/q/openapi", "/q/health", "/q/health/live"})
    void can_reach_public_resource_when_no_user(String expectedPublicPath) {
        given()
                .when()
                .get(expectedPublicPath)
                .then()
                .statusCode(200);
    }
}

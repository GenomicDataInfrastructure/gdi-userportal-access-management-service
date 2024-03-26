// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AttachFileToApplicationTest extends BaseTest {

    @Test
    void can_attach_file_to_application() throws Exception {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .multiPart("file", attachment(), "text/plain")
                .when()
                .post("/api/v1/applications/1/attachments")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void cannot_attach_file_to_application_when_anonymous_request() throws Exception {
        given()
                .multiPart("file", attachment(), "text/plain")
                .when()
                .post("/api/v1/applications/1/attachments")
                .then()
                .statusCode(401);
    }

    private File attachment() throws Exception {
        var url = getClass().getResource("/dummy.txt");
        return new File(url.toURI());
    }
}

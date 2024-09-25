// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import lombok.SneakyThrows;

@QuarkusTest
class AttachFileToApplicationTest extends BaseTest {

    @Test
    void can_attach_file_to_application() {
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
    void cannot_attach_file_to_application_when_application_not_found() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .multiPart("file", attachment(), "text/plain")
                .when()
                .post("/api/v1/applications/12345/attachments")
                .then()
                .statusCode(404);
    }

    @Test
    void cannot_attach_file_to_application_when_not_same_applicant() {
        given()
                .auth()
                .oauth2(getAccessToken("jdoe"))
                .multiPart("file", attachment(), "text/plain")
                .when()
                .post("/api/v1/applications/1/attachments")
                .then()
                .statusCode(403);
    }

    @Test
    void cannot_attach_file_to_application_when_not_in_submittable_state() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .multiPart("file", attachment(), "text/plain")
                .when()
                .post("/api/v1/applications/2/attachments")
                .then()
                .statusCode(409);
    }

    @Test
    void cannot_attach_file_to_application_when_anonymous_request() {
        given()
                .multiPart("file", attachment(), "text/plain")
                .when()
                .post("/api/v1/applications/1/attachments")
                .then()
                .statusCode(401);
    }

    @SneakyThrows
    private File attachment() {
        var url = getClass().getResource("/dummy.txt");
        return new File(url.toURI());
    }
}

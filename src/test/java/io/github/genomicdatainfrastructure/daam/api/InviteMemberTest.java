// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.InviteMember;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class InviteMemberTest extends BaseTest {

    @Test
    void inviteMember() {
        var inviteMember = InviteMember.builder()
                .name("John")
                .email("john@example.com")
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(inviteMember)
                .when()
                .post("/api/v1/applications/12/invite-member")
                .then()
                .statusCode(204);
    }

    @Test
    void whenApplicationNotFound_inviteMember_returnsNotFound() {
        var inviteMember = InviteMember.builder()
                .name("John")
                .email("john@example.com")
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(inviteMember)
                .when()
                .post("/api/v1/applications/13/invite-member")
                .then()
                .statusCode(404);
    }

    @Test
    void whenUserNotApplicant_inviteMember_returnsForbidden() {
        var inviteMember = InviteMember.builder()
                .name("John")
                .email("john@example.com")
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(inviteMember)
                .when()
                .post("/api/v1/applications/14/invite-member")
                .then()
                .statusCode(403);
    }

    @Test
    void whenRemsFailure_inviteMember_returnsServerError() {
        var inviteMember = InviteMember.builder()
                .name("John")
                .email("john@example.com")
                .build();

        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .contentType("application/json")
                .body(inviteMember)
                .when()
                .post("/api/v1/applications/15/invite-member")
                .then()
                .statusCode(500);
    }
}
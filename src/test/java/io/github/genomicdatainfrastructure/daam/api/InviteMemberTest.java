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
    void can_invite_member() {
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
                .statusCode(200);
    }
}

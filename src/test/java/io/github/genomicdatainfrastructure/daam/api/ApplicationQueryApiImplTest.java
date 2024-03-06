// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ApplicationQueryApiImplTest {

  private final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  @Test
  void unauthorized_when_no_user() {
    given().when().get("/api/v1/applications").then().statusCode(401);
  }

  @Test
  void ok_when_authenticated() {
    given()
        .auth()
        .oauth2(getAccessToken("alice"))
        .when()
        .get("/api/v1/applications")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo("25"))
        .body("[0].title", equalTo("2024/14"))
        .body("[0].currentState", equalTo("application.state/draft"))
        .body("[0].stateChangedAt", equalTo("2024-03-05T19:44:46.208Z"));
  }

  @Test
  public void ok_when_public_resource() {
    given().when().get("/").then().statusCode(200);
  }

  private String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName);
  }
}

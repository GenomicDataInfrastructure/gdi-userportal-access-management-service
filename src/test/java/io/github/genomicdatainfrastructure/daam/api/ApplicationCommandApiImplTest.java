// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.CreateApplication;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import java.util.Arrays;


@QuarkusTest
public class ApplicationCommandApiImplTest {

  private final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  @Test
  void unauthorized_when_no_user() {
    given().when().get("/api/v1/applications/create").then().statusCode(401);
  }


  @Test
  void createApplication_when_authenticated() {
    CreateApplication createApplication = CreateApplication.builder()
    .datasetIds(Arrays.asList("dataset1", "dataset2", "dataset3"))
    .build();
    
    given()
        .auth()
        .oauth2(getAccessToken("alice"))
        .contentType("application/json")
        .body(createApplication)
        .when()
        .post("/api/v1/applications/create")
        .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("application-id", equalTo(12345));
  }

  private String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName);
  }
}
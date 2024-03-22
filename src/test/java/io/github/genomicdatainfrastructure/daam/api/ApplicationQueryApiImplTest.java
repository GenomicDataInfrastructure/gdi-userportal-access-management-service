// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ApplicationQueryApiImplTest {

  private final KeycloakTestClient keycloakClient = new KeycloakTestClient();

  @Test
  public void unauthorized_to_list_applications_when_no_user() {
    given().when().get("/api/v1/applications").then().statusCode(401);
  }

  @Test
  public void can_list_applications_when_authenticated() {
    given()
        .auth()
        .oauth2(getAccessToken("alice"))
        .when()
        .get("/api/v1/applications")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(25))
        .body("[0].title", equalTo("2024/14"))
        .body("[0].currentState", equalTo("application.state/draft"))
        .body("[0].stateChangedAt", equalTo("2024-03-05T19:44:46.208Z"));
  }

  @Test
  public void can_reach_public_resource_when_no_user() {
    given().when().get("/").then().statusCode(200);
  }

  @Test
 public void can_retrieve_an_application_when_authenticated() {
   given()
        .auth()
        .oauth2(getAccessToken("alice"))
        .when()
        .get("/api/v1/applications/28")
        .then()
        .statusCode(200)

        .body("id", equalTo(28))
        .body("description", equalTo("Research proposal for studying the effects of climate change on biodiversity."))
        .body("generatedExternalId", equalTo("APP20240328"))
        .body("permissions", hasItem("application.command/copy-as-new"))
        .body("lastActivity", equalTo("2024-03-20T15:18:24.97Z"))
        .body("roles", hasItem("researcher"))
        .body("createdAt", equalTo("2024-03-20T15:18:24.97Z"))
        .body("modifiedAt", equalTo("2024-03-20T15:18:24.97Z"))
        .body("state", equalTo("application.state/submitted"))

        .body("workflow.id", equalTo(12))
        .body("workflow.type", equalTo("research"))
        .body("workflow.voting", equalTo(null))

        .body("applicant.userId", equalTo("applicant456"))
        .body("applicant.name", equalTo("Robert Johnson"))
        .body("applicant.email", equalTo("robert.johnson@example.com"))

        .body("members[0].memberId", equalTo("33"))
        .body("members[0].name", equalTo("Mike Johnson"))
        .body("members[0].email", equalTo("mike.johnson@gmail.com"))

        .body("invitedMembers[0].name", equalTo("special guest"))
        .body("invitedMembers[0].email", equalTo("special.guest@invited.com"))

        .body("datasets[0].id", equalTo(1))
        .body("datasets[0].title[0].language", equalTo("fi"))
        .body("datasets[0].title[0].name", equalTo("Ilmastonmuutoksen vaikutusten tutkiminen biodiversiteettiin"))
        .body("datasets[0].title[1].language", equalTo("en"))
        .body("datasets[0].title[1].name", equalTo("Studying the Impacts of Climate Change on Biodiversity"))
        .body("datasets[0].url[0].language", equalTo("fi"))
        .body("datasets[0].url[0].name", equalTo("lisätietoja-suomeksi"))
        .body("datasets[0].url[1].language", equalTo("en"))
        .body("datasets[0].url[1].name", equalTo("more-info-in-english"))

        .body("forms[0].id", equalTo(1))
        .body("forms[0].internalName", equalTo("app-form"))
        .body("forms[0].externalTitle[0].language", equalTo("fi"))
        .body("forms[0].externalTitle[0].name", equalTo("Hakemuslomake"))
        .body("forms[0].externalTitle[1].language", equalTo("en"))
        .body("forms[0].externalTitle[1].name", equalTo("Application Form"))

        .body("forms[0].fields[0].id", equalTo("1112"))
        .body("forms[0].fields[0].optional", equalTo(true))
        .body("forms[0].fields[0].private", equalTo(true))
        .body("forms[0].fields[0].visible", equalTo(false))
        .body("forms[0].fields[0].title[0].language", equalTo("fi"))
        .body("forms[0].fields[0].title[0].name", equalTo("Kuvaus"))
        .body("forms[0].fields[0].title[1].language", equalTo("en"))
        .body("forms[0].fields[0].title[1].name", equalTo("Description"))
        .body("forms[0].fields[0].type", equalTo("description"))

        .body("events[0].actorId", equalTo("6"))
        .body("events[0].eventTime", equalTo("2024-03-20T15:18:29.97Z"))
        .body("events[0].eventType", equalTo("application-created"))

        .body("attachments[0].id", equalTo(0))
        .body("attachments[0].filename", equalTo("research-proposal.pdf"))
        .body("attachments[0].type", equalTo("pdf"))

        .body("licenses[0].type", equalTo("text"))
        .body("licenses[0].title[0].language", equalTo("fi"))
        .body("licenses[0].title[0].name", equalTo("Lisenssi"))
        .body("licenses[0].title[1].language", equalTo("en"))
        .body("licenses[0].title[1].name",equalTo("License"))
        .body("licenses[0].enabled", equalTo(true))
        .body("licenses[0].archived", equalTo(true));
  }

  @Test
    public void unauthorized_to_retrieve_an_application_when_no_user() {
        given().when().get("/api/v1/applications/28").then().statusCode(401);
  }

  @Test
  public void cannot_retrieve_non_existing_application() {
    given()
        .auth()
        .oauth2(getAccessToken("alice"))
        .when()
        .get("/api/v1/applications/73263")
        .then()
        .statusCode(404);
  }

  private String getAccessToken(String userName) {
    return keycloakClient.getAccessToken(userName);
  }
}

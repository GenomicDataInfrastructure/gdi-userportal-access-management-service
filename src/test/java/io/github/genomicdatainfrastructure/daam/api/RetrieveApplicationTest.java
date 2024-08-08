// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
class RetrieveApplicationTest extends BaseTest {

    @Test
    void can_retrieve_an_application_when_authenticated() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/applications/28")
                .then()
                .statusCode(200)

                .body("id", equalTo(28))
                .body("description", equalTo(
                        "Research proposal for studying the effects of climate change on biodiversity."))
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
                .body("datasets[0].title[0].name", equalTo(
                        "Ilmastonmuutoksen vaikutusten tutkiminen biodiversiteettiin"))
                .body("datasets[0].title[1].language", equalTo("en"))
                .body("datasets[0].title[1].name", equalTo(
                        "Studying the Impacts of Climate Change on Biodiversity"))
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
                .body("forms[0].fields[0].value", equalTo(""))

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
                .body("licenses[0].title[1].name", equalTo("License"))
                .body("licenses[0].enabled", equalTo(true))
                .body("licenses[0].archived", equalTo(true))
                .body("licenses[0].attachmentId[0].language", equalTo("fi"))
                .body("licenses[0].attachmentId[0].name", equalTo("1"))
                .body("licenses[0].attachmentId[1].language", equalTo("en"))
                .body("licenses[0].attachmentId[1].name", equalTo("2"))

                .body("licenses[1].type", equalTo("attachment-filename"))
                .body("licenses[1].title[0].language", equalTo("fi"))
                .body("licenses[1].title[0].name", equalTo("Attachment Lisenssi"))
                .body("licenses[1].title[1].language", equalTo("en"))
                .body("licenses[1].title[1].name", equalTo("Attachment License"))
                .body("licenses[1].enabled", equalTo(true))
                .body("licenses[1].archived", equalTo(false))
                .body("licenses[1].attachmentId[0].language", equalTo("fi"))
                .body("licenses[1].attachmentId[0].name", equalTo("3"))
                .body("licenses[1].attachmentId[1].language", equalTo("en"))
                .body("licenses[1].attachmentId[1].name", equalTo("4"))

                .body("licenses[2].type", equalTo("link"))
                .body("licenses[2].title[0].language", equalTo("fi"))
                .body("licenses[2].title[0].name", equalTo("Link Lisenssi"))
                .body("licenses[2].title[1].language", equalTo("en"))
                .body("licenses[2].title[1].name", equalTo("Link License"))
                .body("licenses[2].enabled", equalTo(false))
                .body("licenses[2].archived", equalTo(true))
                .body("licenses[2].attachmentId[0].language", equalTo("fi"))
                .body("licenses[2].attachmentId[0].name", equalTo("5"))
                .body("licenses[2].attachmentId[1].language", equalTo("en"))
                .body("licenses[2].attachmentId[1].name", equalTo("6"));
    }

    @Test
    void cannot_retrieve_application_when_non_existing_application() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .get("/api/v1/applications/73263")
                .then()
                .statusCode(404);
    }

    @Test
    void cannot_retrieve_application_when_anonymous_request() {
        given()
                .when()
                .get("/api/v1/applications/28")
                .then()
                .statusCode(401);
    }
}

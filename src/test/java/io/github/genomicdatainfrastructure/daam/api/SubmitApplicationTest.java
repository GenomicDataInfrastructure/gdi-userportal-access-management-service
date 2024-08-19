// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;
import io.github.genomicdatainfrastructure.daam.model.ValidationWarning;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class SubmitApplicationTest extends BaseTest {

    @Test
    void can_submit_application() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .post("/api/v1/applications/1/submit")
                .then()
                .statusCode(204);
    }

    @Test
    void cannot_submit_application_when_application_not_found() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .post("/api/v1/applications/12345/submit")
                .then()
                .statusCode(404);
    }

    @Test
    void cannot_submit_application_when_not_same_applicant() {
        given()
                .auth()
                .oauth2(getAccessToken("jdoe"))
                .when()
                .post("/api/v1/applications/1/submit")
                .then()
                .statusCode(403);
    }

    @Test
    void cannot_submit_application_when_not_in_submittable_state() {
        given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .post("/api/v1/applications/2/submit")
                .then()
                .statusCode(428);
    }

    @Test
    void cannot_submit_application_when_anonymous_request() {
        given()
                .when()
                .post("/api/v1/applications/1/submit")
                .then()
                .statusCode(401);
    }

    @Test
    void cannot_submit_application_due_to_submission_errors() {
        var response = given()
                .auth()
                .oauth2(getAccessToken("alice"))
                .when()
                .post("api/v1/applications/44/submit");

        var actual = response.getBody().as(ErrorResponse.class);

        var expected = ErrorResponse.builder()
                .status(400)
                .title("Application could not be submitted")
                .detail("The application could not be submitted.")
                .validationWarnings(List.of(
                        ValidationWarning.builder()
                                .key("Missing")
                                .formId(1L)
                                .fieldId("requiredField")
                                .build(),
                        ValidationWarning.builder()
                                .key("not-accepted-licenses")
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}

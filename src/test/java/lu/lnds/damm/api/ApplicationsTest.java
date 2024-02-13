// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package lu.lnds.damm.api;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import lu.lnds.daam.beans.ListedApplication;
import lu.lnds.daam.beans.RetrievedApplication;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@QuarkusTest
public class ApplicationsTest {

        @Test
        void retrieves_applications() {
                var response = given().when().get("/api/v1/applications").then().statusCode(200);

                var actual = response.extract().body().<List<ListedApplication>>as(List.class);

                assertThat(actual).isEmpty();
        }

        @Test
        void given_id_retrieves_application() {
                var response = given().when().get("/api/v1/applications/dummy").then()
                                .statusCode(200);

                var actual = response.extract().body().as(RetrievedApplication.class);

                var excepted = RetrievedApplication.builder().id("dummy").build();

                assertThat(actual).usingRecursiveComparison().isEqualTo(excepted);
        }
}

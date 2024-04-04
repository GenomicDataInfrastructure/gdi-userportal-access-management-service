// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import io.github.genomicdatainfrastructure.daam.model.*;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class RemsApplicationMapperTest {

    private RemsApplicationMapper underTest;

    @BeforeEach
    private void setUp() {
        underTest = new RemsApplicationMapper();
    }

    @Test
    void should_map_correctly_complete_rems_application_to_retrieved_application() {
        RetrievedApplication retrievedApplication = underTest.from(createApplication());

        assertThat(retrievedApplication.getId()).isEqualTo(1L);
        assertThat(retrievedApplication.getExternalId()).isEqualTo("APP20240328");
        assertThat(retrievedApplication.getDescription()).isEqualTo(
                "Research proposal for studying the effects of climate change on biodiversity.");
        assertThat(retrievedApplication.getGeneratedExternalId()).isEqualTo("APP20240328");
        assertThat(retrievedApplication.getLastActivity()).isEqualTo(OffsetDateTime.parse(
                "2024-03-20T15:18:24.97Z"));
        assertThat(retrievedApplication.getRoles()).containsExactly("researcher");
        assertThat(retrievedApplication.getCreatedAt()).isEqualTo(OffsetDateTime.parse(
                "2022-06-20T15:18:24.97Z"));
        assertThat(retrievedApplication.getModifiedAt()).isEqualTo(OffsetDateTime.parse(
                "2024-03-20T15:18:24.97Z"));
        assertThat(retrievedApplication.getState()).isEqualTo(
                RetrievedApplication.StateEnum.APPROVED);
        assertThat(retrievedApplication.getWorkflow()).isEqualTo(new RetrievedApplicationWorkflow(
                1L, "workflowType"));
        assertThat(retrievedApplication.getApplicant()).isEqualTo(new RetrievedApplicationApplicant(
                "3P331", "John", "john.wattson@gmail.com"));

        assertThat(retrievedApplication.getMembers()).containsExactlyInAnyOrder(
                new RetrievedApplicationMember("5YTR", "Mike", "mike.berry@gmail.com"),
                new RetrievedApplicationMember("NZRWRR", "Elly", "elly.white@outlook.com"));

        assertThat(retrievedApplication.getDatasets()).containsExactlyInAnyOrder(
                new ApplicationDataset(3L, "external-id-3L",
                        List.of(new Label("fi", "Title in Finnish"), new Label("en",
                                "Title in English")),
                        List.of(new Label("fi", "Url info in Finnish"), new Label("en",
                                "Url info in English"))),
                new ApplicationDataset(111L, "external-id-3L", List.of(new Label("fr",
                        "Title in French"),
                        new Label("de", "Title in German")), List.of(new Label("fr",
                                "Url info in French"), new Label("de", "Url info in German"))));

        assertThat(retrievedApplication.getForms()).containsExactlyInAnyOrder(
                new RetrievedApplicationForm(
                        4L,
                        "form-access-1",
                        List.of(new Label("en", "External title in English"), new Label("fr",
                                "External title in French")),
                        List.of(new RetrievedApplicationFormField("98", true, false, true,
                                List.of(new Label("en", "Field in English"), new Label("fr",
                                        "Field in French")), "email"))));

        assertThat(retrievedApplication.getInvitedMembers()).containsExactlyInAnyOrder(
                new RetrievedApplicationInvitedMember("Kate", "kate.tominay@gmail.com"),
                new RetrievedApplicationInvitedMember("Tom", "tom.dylan@gmail.com"));

        assertThat(retrievedApplication.getPermissions()).containsExactlyInAnyOrder(
                "application.command/approve", "application.command/close");

        assertThat(retrievedApplication.getEvents()).containsExactlyInAnyOrder(
                new RetrievedApplicationEvent("1444", OffsetDateTime.parse(
                        "2023-03-20T15:18:24.97Z"), "request"));

        assertThat(retrievedApplication.getAttachments()).containsExactlyInAnyOrder(
                new RetrievedApplicationAttachment(14L, "file.pdf", "pdf"));

        assertThat(retrievedApplication.getLicenses()).isNull();
    }

    private Application createApplication() {
        return Application
                .builder()
                .applicationId(1L)
                .applicationExternalId("APP20240328")
                .applicationDescription(
                        "Research proposal for studying the effects of climate change on biodiversity.")
                .applicationGeneratedExternalId("APP20240328")
                .applicationLastActivity(OffsetDateTime.parse("2024-03-20T15:18:24.97Z"))
                .applicationRoles(Set.of("researcher"))
                .applicationCreated(OffsetDateTime.parse("2022-06-20T15:18:24.97Z"))
                .applicationModified(OffsetDateTime.parse("2024-03-20T15:18:24.97Z"))
                .applicationState(Application.ApplicationStateEnum.APPROVED)
                .applicationWorkflow(new Response10953Workflow(1L, "workflowType", null, null,
                        null))
                .applicationApplicant(new UserWithAttributes("3P331", "John",
                        "john.wattson@gmail.com", null, null, null))
                .applicationMembers(createMembers())
                .applicationResources(createResources())
                .applicationForms(createForms())
                .applicationInvitedMembers(createInvitedMembers())
                .applicationPermissions(createPermissions())
                .applicationEvents(createEvents())
                .applicationAttachments(List.of(new ApplicationAttachment(14L, "file.pdf",
                        "pdf", null, null, null, null)))
                .applicationLicenses(null)
                .build();
    }

    private Set<UserWithAttributes> createMembers() {
        return Set.of(
                new UserWithAttributes("5YTR", "Mike", "mike.berry@gmail.com", null, null, null),
                new UserWithAttributes("NZRWRR", "Elly", "elly.white@outlook.com", null, null,
                        null));
    }

    private List<V2Resource> createResources() {
        var datasetTitleResourceId3 = new LinkedHashMap<String, String>();
        datasetTitleResourceId3.put("fi", "Title in Finnish");
        datasetTitleResourceId3.put("en", "Title in English");

        var datasetUrlInfoResourceId3 = new LinkedHashMap<String, String>();
        datasetUrlInfoResourceId3.put("fi", "Url info in Finnish");
        datasetUrlInfoResourceId3.put("en", "Url info in English");

        var datasetTitleResourceId111 = new LinkedHashMap<String, String>();
        datasetTitleResourceId111.put("fr", "Title in French");
        datasetTitleResourceId111.put("de", "Title in German");

        var datasetUrlInfoResourceId111 = new LinkedHashMap<String, String>();
        datasetUrlInfoResourceId111.put("fr", "Url info in French");
        datasetUrlInfoResourceId111.put("de", "Url info in German");

        return List.of(
                new V2Resource(null, null, null, null, datasetTitleResourceId3, null,
                        datasetUrlInfoResourceId3, "external-id-3L", null, null, 3L),
                new V2Resource(null, null, null, null, datasetTitleResourceId111, null,
                        datasetUrlInfoResourceId111, "external-id-111L", null, null, 111L));
    }

    private List<Form> createForms() {
        var formTitleResourceId4 = new LinkedHashMap<String, String>();
        formTitleResourceId4.put("en", "External title in English");
        formTitleResourceId4.put("fr", "External title in French");

        var fieldTitleResourceId4 = new LinkedHashMap<String, String>();
        fieldTitleResourceId4.put("en", "Field in English");
        fieldTitleResourceId4.put("fr", "Field in French");

        return List.of(
                new Form(4L, "form-access-1", formTitleResourceId4,
                        List.of(new Field(false, fieldTitleResourceId4, true,
                                Field.FieldTypeEnum.EMAIL, "ulrich.smith@outlook.de", "98", true)))
        );
    }

    private Set<Response10953InvitedMembers> createInvitedMembers() {
        return Set.of(
                new Response10953InvitedMembers("Kate", "kate.tominay@gmail.com"),
                new Response10953InvitedMembers("Tom", "tom.dylan@gmail.com"));
    }

    private Set<Application.ApplicationPermissionsEnum> createPermissions() {
        return Set.of(
                Application.ApplicationPermissionsEnum.APPLICATION_PERIOD_COMMAND_SLASH_APPROVE,
                Application.ApplicationPermissionsEnum.APPLICATION_PERIOD_COMMAND_SLASH_CLOSE);
    }

    private List<Event> createEvents() {
        return List.of(
                new Event("request", OffsetDateTime.parse("2023-03-20T15:18:24.97Z"), null, null,
                        new UserWithAttributes("1444", "Jean", "jean.du@gmail.com", null, null,
                                null)));
    }
}

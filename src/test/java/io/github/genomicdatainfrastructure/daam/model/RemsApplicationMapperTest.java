// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.model;

import io.github.genomicdatainfrastructure.daam.remote.rems.model.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RemsApplicationMapperTest {
    private static final ApplicationMapper mapper = new RemsApplicationMapper();

    @Test
    public void should_map_correctly_complete_rems_application_to_retrieved_application() {
        RetrievedApplication retrievedApplication = mapper.from(createApplication());

        assertEquals(1L, retrievedApplication.getId());
        assertEquals("APP20240328", retrievedApplication.getExternalId());
        assertEquals("Research proposal for studying the effects of climate change on biodiversity.", retrievedApplication.getDescription());
        assertEquals("APP20240328", retrievedApplication.getGeneratedExternalId());
        assertEquals(OffsetDateTime.parse("2024-03-20T15:18:24.97Z"), retrievedApplication.getLastActivity());
        assertEquals(Set.of("researcher"), retrievedApplication.getRoles());
        assertEquals(OffsetDateTime.parse("2022-06-20T15:18:24.97Z"), retrievedApplication.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2024-03-20T15:18:24.97Z"), retrievedApplication.getModifiedAt());
        assertEquals(RetrievedApplication.StateEnum.APPROVED, retrievedApplication.getState());
        assertEquals(new RetrievedApplicationWorkflow(1L, "workflowType"), retrievedApplication.getWorkflow());
        assertEquals(new RetrievedApplicationApplicant("3P331", "John", "john.wattson@gmail.com"), retrievedApplication.getApplicant());
        assertEquals(List.of(
                new RetrievedApplicationMember("5YTR", "Mike", "mike.berry@gmail.com"),
                new RetrievedApplicationMember("NZRWRR", "Elly", "elly.white@outlook.com")), retrievedApplication.getMembers());

        assertEquals(List.of(
                new RetrievedApplicationDataset(3L, List.of(new Label("fi", "Title in Finnish"), new Label("en", "Title in English")), List.of(new Label("fi", "Url info in Finnish"), new Label("en", "Url info in English"))),
                new RetrievedApplicationDataset(111L, List.of(new Label("fr", "Title in French"), new Label("de", "Title in German")), List.of(new Label("fr", "Url info in French"), new Label("de", "Url info in German")))), retrievedApplication.getDatasets());

        assertEquals(List.of(new RetrievedApplicationForm(4L, "form-access-1", List.of(new Label("en", "External title in English"),
                                                                                                      new Label("fr", "External title in French")),
                             List.of(new RetrievedApplicationFormField("98", true, false, true,
                                     List.of(new Label("en", "Field in English"),
                                             new Label("fr", "Field in French")) , "email")))),
                retrievedApplication.getForms());

         assertEquals(List.of(
                 new RetrievedApplicationInvitedMember("Kate", "kate.tominay@gmail.com"),
                 new RetrievedApplicationInvitedMember("Tom", "tom.dylan@gmail.com")),
                 retrievedApplication.getInvitedMembers());
        assertEquals(List.of("application.command/approve", "application.command/close"), retrievedApplication.getPermissions());
        assertEquals(List.of(new RetrievedApplicationEvent("1444", OffsetDateTime.parse("2023-03-20T15:18:24.97Z"), "request")), retrievedApplication.getEvents());
        assertEquals(List.of(new RetrievedApplicationAttachment(14L, "file.pdf", "pdf")), retrievedApplication.getAttachments());
        assertEquals(null, retrievedApplication.getLicenses());
    }
    private Application createApplication() {
        Application remsApplication = new Application();
        remsApplication.setApplicationId(1L);
        remsApplication.setApplicationExternalId("APP20240328");
        remsApplication.setApplicationDescription("Research proposal for studying the effects of climate change on biodiversity.");
        remsApplication.setApplicationGeneratedExternalId("APP20240328");
        remsApplication.setApplicationLastActivity(OffsetDateTime.parse("2024-03-20T15:18:24.97Z"));
        remsApplication.setApplicationRoles(Set.of("researcher"));
        remsApplication.setApplicationCreated(OffsetDateTime.parse("2022-06-20T15:18:24.97Z"));
        remsApplication.setApplicationModified(OffsetDateTime.parse("2024-03-20T15:18:24.97Z"));
        remsApplication.setApplicationState(Application.ApplicationStateEnum.APPROVED);
        remsApplication.setApplicationWorkflow(new Response10953Workflow(1L, "workflowType", null, null, null));
        remsApplication.setApplicationApplicant(new UserWithAttributes("3P331", "John", "john.wattson@gmail.com", null, null, null));

        remsApplication.setApplicationMembers(
                new LinkedHashSet<>(
                        List.of(
                                new UserWithAttributes("5YTR", "Mike", "mike.berry@gmail.com", null, null, null ),
                                new UserWithAttributes("NZRWRR", "Elly", "elly.white@outlook.com", null, null, null))));


        var DatasetTitleResourceId3 = new LinkedHashMap<String, String>();
        DatasetTitleResourceId3.put("fi", "Title in Finnish");
        DatasetTitleResourceId3.put("en", "Title in English");

        var DatasetUrlInfoResourceId3 = new LinkedHashMap<String, String>();
        DatasetUrlInfoResourceId3.put("fi", "Url info in Finnish");
        DatasetUrlInfoResourceId3.put("en", "Url info in English");

        var DatasetTitleResourceId111 = new LinkedHashMap<String, String>();
        DatasetTitleResourceId111.put("fr", "Title in French");
        DatasetTitleResourceId111.put("de", "Title in German");

        var DatasetUrlInfoResourceId111 = new LinkedHashMap<String, String>();
        DatasetUrlInfoResourceId111.put("fr", "Url info in French");
        DatasetUrlInfoResourceId111.put("de", "Url info in German");

        remsApplication.setApplicationResources(List.of(
                new V2Resource(null, null, null, null, DatasetTitleResourceId3, null, DatasetUrlInfoResourceId3, null, null, null, 3L),
                new V2Resource(null, null, null, null, DatasetTitleResourceId111, null, DatasetUrlInfoResourceId111, null, null, null, 111L)));

        var formTitleResourceId4 = new LinkedHashMap<String, String>();
        formTitleResourceId4.put("en", "External title in English");
        formTitleResourceId4.put("fr", "External title in French");

        var fieldTitleResourceId4 = new LinkedHashMap<String, String>();
        fieldTitleResourceId4.put("en", "Field in English");
        fieldTitleResourceId4.put("fr", "Field in French");

        remsApplication.setApplicationForms(List.of(
                new Form(4L,
                        "form-access-1",
                        formTitleResourceId4,
                        List.of(new Field(false,
                                fieldTitleResourceId4,
                                true,
                                Field.FieldTypeEnum.EMAIL,
                                "ulrich.smith@outlook.de",
                                "98",
                                true)))
        ));

        remsApplication.setApplicationInvitedMembers(new LinkedHashSet(List.of(new Response10953InvitedMembers("Kate", "kate.tominay@gmail.com"), new Response10953InvitedMembers("Tom", "tom.dylan@gmail.com"))));

        remsApplication.setApplicationPermissions(new LinkedHashSet<>(List.of(Application.ApplicationPermissionsEnum.APPLICATION_PERIOD_COMMAND_SLASH_APPROVE, Application.ApplicationPermissionsEnum.APPLICATION_PERIOD_COMMAND_SLASH_CLOSE)));

        remsApplication.setApplicationEvents(List.of(
                new Event("request", OffsetDateTime.parse("2023-03-20T15:18:24.97Z"), null, null, new UserWithAttributes("1444", "Jean", "jean.du@gmail.com", null, null, null))));
        remsApplication.setApplicationAttachments(List.of(new ApplicationAttachment(14L, "file.pdf", "pdf", null, null, null, null)));
        remsApplication.setApplicationLicenses(null);
        return remsApplication;
    }
}

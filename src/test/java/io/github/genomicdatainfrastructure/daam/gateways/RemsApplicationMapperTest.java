// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.daam.model.*;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RemsApplicationMapperTest {

    private RemsApplicationMapper underTest;

    @BeforeEach
    void setUp() {
        underTest = new RemsApplicationMapper(new ObjectMapper());
    }

    @Test
    void should_map_correctly_complete_rems_application_to_retrieved_application() {
        var retrievedApplication = underTest.from("dummy", createApplication());

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
                RetrievedApplicationState.APPROVED);
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
                new ApplicationDataset(111L, "external-id-111L", List.of(new Label("fr",
                        "Title in French"),
                        new Label("de", "Title in German")), List.of(new Label("fr",
                                "Url info in French"), new Label("de", "Url info in German"))));

        assertThat(retrievedApplication.getForms())
                .containsExactlyInAnyOrder(RetrievedApplicationForm.builder()
                        .id(4L)
                        .internalName("form-access-1")
                        .externalTitle(List.of(
                                new Label("en", "External title in English"),
                                new Label("fr", "External title in French")
                        ))
                        .fields(List.of(
                                RetrievedApplicationFormField.builder()
                                        .id("98")
                                        .value("ulrich.smith@outlook.de")
                                        .optional(true)
                                        ._private(false)
                                        .visible(true)
                                        .title(List.of(
                                                new Label("en", "Field in English"),
                                                new Label("fr", "Field in French")
                                        ))
                                        .type(FormFieldType.EMAIL)
                                        .options(List.of())
                                        .tableValues(List.of())
                                        .tableColumns(List.of())
                                        .infoText(List.of())
                                        .placeholder(List.of())
                                        .build(),
                                RetrievedApplicationFormField.builder()
                                        .id("99")
                                        .infoText(List.of(
                                                new Label("en", "Additional information")
                                        ))
                                        .placeholder(List.of(
                                                new Label("en", "Placeholder")
                                        ))
                                        .maxLength(100L)
                                        .privacy(FormFieldPrivacy.PRIVATE)
                                        .tableValues(List.of())
                                        .tableColumns(List.of())
                                        .options(List.of(
                                                FormFieldOption.builder()
                                                        .key("option1")
                                                        .label(List.of(Label.builder()
                                                                .language("en")
                                                                .name("option 1")
                                                                .build()))
                                                        .build(),
                                                FormFieldOption.builder()
                                                        .key("option2")
                                                        .label(List.of(Label.builder()
                                                                .language("en")
                                                                .name("option 2")
                                                                .build()))
                                                        .build()
                                        ))
                                        .optional(true)
                                        ._private(false)
                                        .visible(true)
                                        .title(List.of(
                                                new Label("en", "Field in English"),
                                                new Label("fr", "Field in French")
                                        ))
                                        .type(FormFieldType.MULTISELECT)
                                        .build(),
                                RetrievedApplicationFormField.builder()
                                        .id("100")
                                        .tableValues(List.of(
                                                List.of(
                                                        FormFieldTableValue.builder()
                                                                .column("column1")
                                                                .value("value1")
                                                                .build(),
                                                        FormFieldTableValue.builder()
                                                                .column("column2")
                                                                .value("value2")
                                                                .build()
                                                )
                                        ))
                                        .tableColumns(List.of(
                                                FormFieldTableColumn.builder()
                                                        .key("column1")
                                                        .label(List.of(
                                                                new Label("en", "Column 1")
                                                        ))
                                                        .build(),
                                                FormFieldTableColumn.builder()
                                                        .key("column2")
                                                        .label(List.of(
                                                                new Label("en", "Column 2")
                                                        ))
                                                        .build()
                                        ))
                                        .options(List.of())
                                        .optional(true)
                                        ._private(false)
                                        .visible(true)
                                        .title(List.of(
                                                new Label("en", "Field in English"),
                                                new Label("fr", "Field in French")
                                        ))
                                        .type(FormFieldType.TABLE)
                                        .infoText(List.of())
                                        .placeholder(List.of())
                                        .build()
                        ))
                        .build());

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

        assertThat(retrievedApplication.getLicenses()).isEmpty();
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
                Form.builder()
                        .formId(4L)
                        .formInternalName("form-access-1")
                        .formExternalTitle(formTitleResourceId4)
                        .formFields(List.of(
                                Field.builder()
                                        .fieldId("98")
                                        .fieldValue("ulrich.smith@outlook.de")
                                        .fieldOptional(true)
                                        .fieldPrivate(false)
                                        .fieldVisible(true)
                                        .fieldTitle(fieldTitleResourceId4)
                                        .fieldType(Field.FieldTypeEnum.EMAIL)
                                        .build(),
                                Field.builder()
                                        .fieldId("99")
                                        .fieldInfoText(Map.of("en", "Additional information"))
                                        .fieldPlaceholder(Map.of("en", "Placeholder"))
                                        .fieldMaxLength(100L)
                                        .fieldPrivacy(Field.FieldPrivacyEnum.PRIVATE)
                                        .fieldOptions(List.of(
                                                FormTemplateFieldOption.builder()
                                                        .key("option1")
                                                        .label(Map.of("en", "option 1"))
                                                        .build(),
                                                FormTemplateFieldOption.builder()
                                                        .key("option2")
                                                        .label(Map.of("en", "option 2"))
                                                        .build()
                                        ))
                                        .fieldOptional(true)
                                        .fieldPrivate(false)
                                        .fieldVisible(true)
                                        .fieldTitle(fieldTitleResourceId4)
                                        .fieldType(Field.FieldTypeEnum.MULTISELECT)
                                        .build(),
                                Field.builder()
                                        .fieldId("100")
                                        .fieldValue(List.of(
                                                List.of(
                                                        FormTemplateTableValue.builder()
                                                                .column("column1")
                                                                .value("value1")
                                                                .build(),
                                                        FormTemplateTableValue.builder()
                                                                .column("column2")
                                                                .value("value2")
                                                                .build()
                                                )
                                        ))
                                        .fieldColumns(List.of(
                                                FormTemplateFieldColumn.builder()
                                                        .key("column1")
                                                        .label(Map.of("en", "Column 1"))
                                                        .build(),
                                                FormTemplateFieldColumn.builder()
                                                        .key("column2")
                                                        .label(Map.of("en", "Column 2"))
                                                        .build()
                                        ))
                                        .fieldOptional(true)
                                        .fieldPrivate(false)
                                        .fieldVisible(true)
                                        .fieldTitle(fieldTitleResourceId4)
                                        .fieldType(Field.FieldTypeEnum.TABLE)
                                        .build()
                        ))
                        .build()
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

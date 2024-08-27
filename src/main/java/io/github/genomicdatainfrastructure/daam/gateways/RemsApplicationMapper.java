// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.genomicdatainfrastructure.daam.model.*;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static java.util.Optional.ofNullable;

@ApplicationScoped
@RequiredArgsConstructor
public class RemsApplicationMapper {

    private final ObjectMapper mapper;

    public RetrievedApplication from(String userId, Application application) {
        return RetrievedApplication
                .builder()
                .id(application.getApplicationId())
                .externalId(application.getApplicationExternalId())
                .description(application.getApplicationDescription())
                .generatedExternalId(application.getApplicationGeneratedExternalId())
                .lastActivity(application.getApplicationLastActivity())
                .roles(application.getApplicationRoles())
                .createdAt(application.getApplicationCreated())
                .modifiedAt(application.getApplicationModified())
                .workflow(toWorkflow(application))
                .applicant(toApplicant(application))
                .members(toMembers(application))
                .invitedMembers(toInvitedMembers(application))
                .datasets(toDatasets(application))
                .forms(toForms(application))
                .permissions(toPermissions(application))
                .events(toEvents(application))
                .attachments(toAttachments(application))
                .licenses(toLicences(userId, application))
                .state(toState(application))
                .build();
    }

    public List<ListedApplication> from(List<ApplicationOverview> applicationOverviews) {
        return applicationOverviews
                .stream()
                .map(this::from)
                .toList();
    }

    private ListedApplication from(ApplicationOverview applicationOverview) {
        return ListedApplication.builder()
                .id(applicationOverview.getApplicationId())
                .title(applicationOverview.getApplicationExternalId())
                .description(applicationOverview.getApplicationDescription())
                .currentState(applicationOverview.getApplicationState().value())
                .stateChangedAt(applicationOverview.getApplicationLastActivity())
                .createdAt(applicationOverview.getApplicationCreated())
                .datasets(applicationOverview.getApplicationResources()
                        .stream()
                        .map(this::toDataset)
                        .toList())
                .build();
    }

    private RetrievedApplicationWorkflow toWorkflow(Application application) {
        var workflow = ofNullable(application
                .getApplicationWorkflow());

        return new RetrievedApplicationWorkflow(workflow.map(Response10953Workflow::getWorkflowId)
                .orElse(null),
                workflow.map(Response10953Workflow::getWorkflowType).orElse(null));
    }

    private RetrievedApplicationApplicant toApplicant(Application application) {
        var applicant = ofNullable(application
                .getApplicationApplicant());

        return new RetrievedApplicationApplicant(
                applicant.map(UserWithAttributes::getUserid).orElse(null),
                applicant.map(UserWithAttributes::getName).orElse(null),
                applicant.map(UserWithAttributes::getEmail).orElse(null));
    }

    private List<RetrievedApplicationMember> toMembers(Application application) {
        var potentialMembers = ofNullable(application
                .getApplicationMembers());

        return potentialMembers.map(members -> members
                .stream()
                .map(this::toMember)
                .toList())
                .orElse(null);
    }

    private RetrievedApplicationMember toMember(UserWithAttributes member) {
        var potentialMember = ofNullable(member);

        return new RetrievedApplicationMember(potentialMember.map(
                UserWithAttributes::getUserid).orElse(null),
                potentialMember.map(UserWithAttributes::getName).orElse(null),
                potentialMember.map(UserWithAttributes::getEmail).orElse(null));
    }

    private List<RetrievedApplicationInvitedMember> toInvitedMembers(Application application) {
        var potentialInvitedMembers = ofNullable(
                application.getApplicationInvitedMembers());

        return potentialInvitedMembers.map(invitedMembers -> invitedMembers
                .stream()
                .map(this::toInvitedMember)
                .toList())
                .orElse(null);
    }

    private RetrievedApplicationInvitedMember toInvitedMember(
            Response10953InvitedMembers invitedMember) {
        var potentialInvitedMember = ofNullable(invitedMember);

        return new RetrievedApplicationInvitedMember(
                potentialInvitedMember.map(Response10953InvitedMembers::getName).orElse(
                        null),
                potentialInvitedMember.map(Response10953InvitedMembers::getEmail)
                        .orElse(null));
    }

    private List<ApplicationDataset> toDatasets(Application application) {
        var potentialResources = ofNullable(application
                .getApplicationResources());

        return potentialResources
                .map(resources -> resources
                        .stream()
                        .map(this::toDataset)
                        .toList())
                .orElse(null);
    }

    private ApplicationDataset toDataset(V2Resource resource) {
        var potentialResource = ofNullable(resource);

        return new ApplicationDataset(potentialResource.map(
                V2Resource::getCatalogueItemId).orElse(null),
                potentialResource.map(V2Resource::getResourceExtId).orElse(null),
                potentialResource.map(r -> toLabelObject(r
                        .getCatalogueItemTitle())).orElse(null),
                potentialResource.map(r -> toLabelObject(r
                        .getCatalogueItemInfourl())).orElse(null));
    }

    private List<RetrievedApplicationForm> toForms(Application application) {
        var potentialForms = ofNullable(application
                .getApplicationForms());

        return potentialForms.map(forms -> forms
                .stream()
                .map(this::toForm)
                .toList())
                .orElse(null);
    }

    private RetrievedApplicationForm toForm(Form form) {
        var potentialForm = ofNullable(form);

        return new RetrievedApplicationForm(potentialForm.map(Form::getFormId).orElse(null),
                potentialForm.map(Form::getFormInternalName).orElse(null),
                potentialForm.map(f -> toLabelObject(f.getFormExternalTitle())).orElse(null),
                potentialForm.map(this::toFormFields).orElse(null));
    }

    private List<RetrievedApplicationFormField> toFormFields(Form form) {
        var potentialFields = ofNullable(form.getFormFields());

        return potentialFields.map(fields -> fields
                .stream()
                .filter(Objects::nonNull)
                .map(this::toFormField)
                .toList())
                .orElse(null);
    }

    private RetrievedApplicationFormField toFormField(Field formField) {
        var type = formField.getFieldType();

        var value = formField.getFieldValue();

        String stringValue = null;
        var tableValues = List.<List<FormFieldTableValue>>of();

        if (Field.FieldTypeEnum.TABLE == type) {
            tableValues = mapper.convertValue(value, new TypeReference<>() {
            });
        } else {
            stringValue = ofNullable(value).map(Object::toString).orElse(null);
        }

        return RetrievedApplicationFormField.builder()
                .id(formField.getFieldId())
                .value(stringValue)
                .optional(formField.getFieldOptional())
                ._private(formField.getFieldPrivate())
                .visible(formField.getFieldVisible())
                .title(toLabelObject(formField.getFieldTitle()))
                .type(ofNullable(type).map(Field.FieldTypeEnum::value).orElse(null))
                .tableValues(tableValues)
                .infoText(toLabelObject(formField.getFieldInfoText()))
                .placeholder(toLabelObject(formField.getFieldPlaceholder()))
                .maxLength(formField.getFieldMaxLength())
                .privacy(toPrivacy(formField))
                .options(toOptions(formField))
                .tableColumns(toColumns(formField))
                .build();
    }

    private RetrievedApplicationFormField.PrivacyEnum toPrivacy(Field formField) {
        return ofNullable(formField)
                .map(Field::getFieldPrivacy)
                .map(it -> RetrievedApplicationFormField.PrivacyEnum.fromString(it.value()))
                .orElse(null);
    }

    private List<FormFieldOption> toOptions(Field formField) {
        var nonNullOptions = ofNullable(formField)
                .map(Field::getFieldOptions)
                .orElseGet(List::of);

        return nonNullOptions.stream()
                .filter(Objects::nonNull)
                .map(it -> FormFieldOption.builder()
                        .key(it.getKey())
                        .label(toLabelObject(it.getLabel()))
                        .build())
                .toList();
    }

    private List<FormFieldTableColumn> toColumns(Field formField) {
        var nonNullOptions = ofNullable(formField)
                .map(Field::getFieldColumns)
                .orElseGet(List::of);

        return nonNullOptions.stream()
                .filter(Objects::nonNull)
                .map(it -> FormFieldTableColumn.builder()
                        .key(it.getKey())
                        .label(toLabelObject(it.getLabel()))
                        .build())
                .toList();
    }

    private List<String> toPermissions(Application application) {
        var potentialPermissions = ofNullable(application.getApplicationPermissions());

        return potentialPermissions.map(permissions -> permissions
                .stream()
                .map(this::toPermission)
                .toList())
                .orElse(null);
    }

    private String toPermission(Application.ApplicationPermissionsEnum permission) {
        var potentialPermission = ofNullable(permission);

        return potentialPermission
                .map(Application.ApplicationPermissionsEnum::value)
                .orElse(null);
    }

    private List<RetrievedApplicationEvent> toEvents(Application application) {
        var potentialEvents = ofNullable(application.getApplicationEvents())
                .orElseGet(List::of);

        return potentialEvents
                .stream()
                .filter(Objects::nonNull)
                .map(this::toEvent)
                .sorted(Comparator.comparing(RetrievedApplicationEvent::getEventTime).reversed())
                .toList();
    }

    private RetrievedApplicationEvent toEvent(Event event) {
        return RetrievedApplicationEvent.builder()
                .actorId(toUserId(event))
                .eventTime(event.getEventTime())
                .eventType(event.getEventType())
                .build();
    }

    private String toUserId(Event event) {
        var eventActorAttributes = ofNullable(event.getEventActorAttributes());
        return eventActorAttributes.map(UserWithAttributes::getUserid).orElse(null);
    }

    private List<RetrievedApplicationAttachment> toAttachments(Application application) {
        var potentialAttachments = ofNullable(application
                .getApplicationAttachments());

        return potentialAttachments.map(attachments -> attachments
                .stream()
                .map(this::toAttachment)
                .toList())
                .orElse(null);
    }

    private RetrievedApplicationAttachment toAttachment(ApplicationAttachment attachment) {
        var potentialAttachment = ofNullable(attachment);

        return new RetrievedApplicationAttachment(potentialAttachment.map(
                ApplicationAttachment::getAttachmentId).orElse(null),
                potentialAttachment.map(ApplicationAttachment::getAttachmentFilename)
                        .orElse(null),
                potentialAttachment.map(ApplicationAttachment::getAttachmentType)
                        .orElse(null));
    }

    private List<RetrievedApplicationLicense> toLicences(String userId, Application application) {
        var potentialLicenses = ofNullable(application.getApplicationLicenses())
                .orElseGet(List::of);

        var acceptedLicenses = application.getApplicationAcceptedLicenses();

        return potentialLicenses
                .stream()
                .filter(Objects::nonNull)
                .map(it -> toLicense(it, userId, acceptedLicenses))
                .toList();
    }

    private RetrievedApplicationLicense toLicense(
            V2License license,
            String userId,
            Map<String, Set<Long>> acceptedLicenses
    ) {
        var nonNullAcceptedLicenses = ofNullable(acceptedLicenses).orElseGet(Map::of);
        var acceptedLicensesByCurrentUser = nonNullAcceptedLicenses.getOrDefault(userId, Set.of());

        return RetrievedApplicationLicense.builder()
                .id(license.getLicenseId())
                .type(toLicenseType(license))
                .title(toLabelObject(license.getLicenseTitle()))
                .enabled(license.getLicenseEnabled())
                .archived(license.getLicenseArchived())
                .link(toLabelObject(license.getLicenseLink()))
                .text(toLabelObject(license.getLicenseText()))
                .attachmentFilename(toLabelObject(license.getLicenseAttachmentFilename()))
                .attachmentId(toLabelObject(license.getLicenseAttachmentId()))
                .acceptedByCurrentUser(acceptedLicensesByCurrentUser.contains(license
                        .getLicenseId()))
                .build();
    }

    private RetrievedApplicationLicense.TypeEnum toLicenseType(V2License license) {
        var licenseType = ofNullable(license.getLicenseType());
        return licenseType.map(V2License.LicenseTypeEnum::value)
                .map(RetrievedApplicationLicense.TypeEnum::fromString)
                .orElse(null);
    }

    private RetrievedApplication.StateEnum toState(Application application) {
        var potentialState = ofNullable(application
                .getApplicationState());

        return potentialState.map(state -> RetrievedApplication.StateEnum.fromString(state.value()))
                .orElse(null);
    }

    private List<Label> toLabelObject(Map<String, ?> map) {
        if (map == null) {
            return List.of();
        }

        return map.entrySet()
                .stream()
                .map(entry -> new Label(entry.getKey(), entry.getValue() != null ? entry.getValue()
                        .toString() : null))
                .toList();
    }

}

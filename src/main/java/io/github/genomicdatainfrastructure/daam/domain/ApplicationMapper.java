// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.domain;

import io.github.genomicdatainfrastructure.daam.model.*;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicationMapper {

    public static RetrievedApplication toRetrievedApplication(Application remsApplication) {
        return RetrievedApplication
                .builder()
                .workflow(toRetrievedApplicationWorkflow(remsApplication.getApplicationWorkflow()))
                .externalId(remsApplication.getApplicationExternalId())
                .id(remsApplication.getApplicationId().toString())
                .applicant(toRetrievedApplicationApplicant(remsApplication.getApplicationApplicant()))
                .members(toRetrievedApplicationMembers(remsApplication.getApplicationMembers()))
                .datasets(toRetrievedApplicationDatasets(remsApplication.getApplicationResources()))
                .acceptedLicenses(toRetrievedApplicationAcceptedLicences(remsApplication.getApplicationAcceptedLicenses()))
                .forms(toRetrievedApplicationForms(remsApplication.getApplicationForms()))
                .invitedMembers(toRetrievedApplicationInvitedMembers(remsApplication.getApplicationInvitedMembers()))
                .description(remsApplication.getApplicationDescription())
                .generatedExternalId(remsApplication.getApplicationGeneratedExternalId())
                .permissions(toRetrievedApplicationPermissions(remsApplication.getApplicationPermissions()))
                .lastActivity(remsApplication.getApplicationLastActivity())
                .events(toRetrievedApplicationEvents(remsApplication.getApplicationEvents()))
                .roles(remsApplication.getApplicationRoles().stream().toList())
                .attachments(toRetrievedApplicationAttachments(remsApplication.getApplicationAttachments()))
                .licenses(toRetrievedApplicationLicences(remsApplication.getApplicationLicenses()))
                .createdAt(remsApplication.getApplicationCreated())
                .state(remsApplication.getApplicationState())
                .modifiedAt(remsApplication.getApplicationModified())
                .build();
    }

    private static RetrievedApplicationWorkflow toRetrievedApplicationWorkflow(Response10953Workflow remsWorkflow) {
        return new RetrievedApplicationWorkflow(remsWorkflow.getWorkflowId().toString(),
                remsWorkflow.getWorkflowType());
    }

    private static RetrievedApplicationApplicant toRetrievedApplicationApplicant(UserWithAttributes remsApplicant) {
        return new RetrievedApplicationApplicant(
                remsApplicant.getUserid(),
                remsApplicant.getName(),
                remsApplicant.getEmail());
    }

    private static List<RetrievedApplicationMember> toRetrievedApplicationMembers(Set<UserWithAttributes> remsMembers) {
        return remsMembers
                .stream()
                .map(member -> new RetrievedApplicationMember(member.getUserid(),
                                                              member.getName(),
                                                              member.getEmail()))
                .toList();
    }

    private static List<RetrievedApplicationDataset> toRetrievedApplicationDatasets(List<V2Resource> remsResources) {
        return remsResources
                .stream()
                .map(resource -> new RetrievedApplicationDataset(resource.getCatalogueItemId().toString(),
                                                                 toLabelObject(resource.getCatalogueItemTitle()),
                                                                 toLabelObject(resource.getCatalogueItemInfourl())))
                .toList();
    }

    private static List<RetrievedAcceptedLicense> toRetrievedApplicationAcceptedLicences(Map<String, Set<Long>> remsAcceptedLicenses) {
        return remsAcceptedLicenses
                .entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream().map(licenseId -> new RetrievedAcceptedLicense(entry.getKey(), licenseId.toString())))
                .toList();
    }

    private static List<RetrievedApplicationForm> toRetrievedApplicationForms(List<Form> remsApplicationForms) {
        return remsApplicationForms
                .stream()
                .map(form -> new RetrievedApplicationForm(form.getFormId().toString(),
                                                         form.getFormInternalName(),
                                                         toLabelObject(form.getFormExternalTitle()),
                                                         form.getFormFields()
                                                                 .stream()
                                                                 .map(ApplicationMapper::toRetrievedApplicationFormField)
                                                                 .toList()))
                .toList();
    }

    private static RetrievedApplicationFormField toRetrievedApplicationFormField(Field remsFormField) {
        return new RetrievedApplicationFormField(remsFormField.getFieldId(),
                                                 remsFormField.getFieldOptional(),
                                                 remsFormField.getFieldPrivate(),
                                                 remsFormField.getFieldVisible(),
                                                 toLabelObject(remsFormField.getFieldTitle()),
                                                 remsFormField.getFieldType().value(),
                                                 remsFormField.getFieldValue().toString());
    }


    private static List<RetrievedApplicationInvitedMember> toRetrievedApplicationInvitedMembers(Set<Response10953InvitedMembers> remsInvitedMembers) {
        return remsInvitedMembers
                .stream()
                .map(invitedMember -> new RetrievedApplicationInvitedMember(
                                                invitedMember.getName(),
                                                invitedMember.getEmail()))
                .toList();
    }

    private static List<String> toRetrievedApplicationPermissions(Set<Application.ApplicationPermissionsEnum> remsApplicationPermissions) {
        return remsApplicationPermissions
                .stream()
                .map(Application.ApplicationPermissionsEnum::value)
                .toList();
    }
    private static List<RetrievedApplicationEvent> toRetrievedApplicationEvents(List<Event> remsApplicationEvents) {
        return remsApplicationEvents
                .stream()
                .map(event -> new RetrievedApplicationEvent(event.getEventId().toString(),
                                                            event.getEventActor(),
                                                            event.getEventTime(),
                                                            event.getEventType()))
                .toList();
    }

    private static List<RetrievedApplicationAttachment> toRetrievedApplicationAttachments(List<ApplicationAttachment> remsApplicationAttachments) {
        return remsApplicationAttachments
                .stream()
                .map(attachment -> new RetrievedApplicationAttachment(attachment.getAttachmentId().toString(),
                                                                     attachment.getAttachmentFilename().toString(),
                                                                     attachment.getAttachmentType()))
                .toList();
    }

    private static List<RetrievedApplicationLicense> toRetrievedApplicationLicences(List<V2License> remsApplicationLicenses) {
        return remsApplicationLicenses
                .stream()
                .map(license -> new RetrievedApplicationLicense(license.getLicenseType().value(),
                                                                toLabelObject(license.getLicenseLink()),
                                                                toLabelObject(license.getLicenseTitle()),
                                                                license.getLicenseEnabled(),
                                                                license.getLicenseArchived()))
                .toList();
    }

    private static List<Label> toLabelObject(Map<String, String> map) {
        return map
                .entrySet()
                .stream()
                .map(entry -> new Label(entry.getKey(), entry.getValue()))
                .toList();
    }

}

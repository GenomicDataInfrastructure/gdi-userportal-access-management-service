// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.model;

import io.github.genomicdatainfrastructure.daam.remote.rems.model.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class RemsApplicationMapper implements ApplicationMapper<Application> {

    @Override
    public RetrievedApplication from(Application application) {
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
                .licenses(toLicences(application))
                .state(toState(application))
                .build();
    }

    private RetrievedApplicationWorkflow toWorkflow(Application application) {
        Optional<Response10953Workflow> workflow = Optional.ofNullable(application.getApplicationWorkflow());

        return new RetrievedApplicationWorkflow
                (workflow.map(Response10953Workflow::getWorkflowId).orElse(null),
                 workflow.map(Response10953Workflow::getWorkflowType).orElse(null));
    }

    private RetrievedApplicationApplicant toApplicant(Application application) {
        Optional<UserWithAttributes> applicant = Optional.ofNullable(application.getApplicationApplicant());

        return new RetrievedApplicationApplicant(
                applicant.map(UserWithAttributes::getUserid).orElse(null),
                applicant.map(UserWithAttributes::getName).orElse(null),
                applicant.map(UserWithAttributes::getEmail).orElse(null));
    }

    private List<RetrievedApplicationMember> toMembers(Application application) {
        Optional<Set<UserWithAttributes>> potentialMembers = Optional.ofNullable(application.getApplicationMembers());

        return potentialMembers.map(members -> members
                        .stream()
                        .map(member -> {
                            Optional<UserWithAttributes> potentialMember = Optional.ofNullable(member);

                            return new RetrievedApplicationMember(potentialMember.map(UserWithAttributes::getUserid).orElse(null),
                                    potentialMember.map(UserWithAttributes::getName).orElse(null),
                                    potentialMember.map(UserWithAttributes::getEmail).orElse(null));
                        })
                        .toList())
                .orElse(null);
    }

    private List<RetrievedApplicationInvitedMember> toInvitedMembers(Application application) {
        Optional<Set<Response10953InvitedMembers>> potentialInvitedMembers = Optional.ofNullable(application.getApplicationInvitedMembers());

        return potentialInvitedMembers.map(invitedMembers -> invitedMembers
                        .stream()
                        .map(invitedMember -> {
                            Optional<Response10953InvitedMembers> potentialInvitedMember = Optional.ofNullable(invitedMember);

                            return new RetrievedApplicationInvitedMember(
                                    potentialInvitedMember.map(Response10953InvitedMembers::getName).orElse(null),
                                    potentialInvitedMember.map(Response10953InvitedMembers::getEmail).orElse(null));
                        })
                        .toList())
                .orElse(null);
    }

    private List<RetrievedApplicationDataset> toDatasets(Application application) {
        Optional<List<V2Resource>> potentialResources = Optional.ofNullable(application.getApplicationResources());

        return potentialResources
                .map(resources -> resources
                        .stream()
                        .map(resource -> {
                            Optional<V2Resource> potentialResource = Optional.ofNullable(resource);

                            return new RetrievedApplicationDataset(potentialResource.map(V2Resource::getCatalogueItemId).orElse(null),
                                    potentialResource.map(r -> toLabelObject(r.getCatalogueItemTitle())).orElse(null),
                                    potentialResource.map(r -> toLabelObject((r.getCatalogueItemInfourl()))).orElse(null));
                        })
                        .toList())
                .orElse(null);
    }

    private List<RetrievedApplicationForm> toForms(Application application) {
        Optional<List<Form>> potentialForms = Optional.ofNullable(application.getApplicationForms());

        return potentialForms.map(forms -> forms
                        .stream()
                        .map(form -> {
                            Optional<Form> potentialForm = Optional.ofNullable(form);

                            return new RetrievedApplicationForm(potentialForm.map(Form::getFormId).orElse(null),
                                        potentialForm.map(Form::getFormInternalName).orElse(null),
                                        potentialForm.map(f -> toLabelObject(f.getFormExternalTitle())).orElse(null),
                                        potentialForm.map(f -> f.getFormFields()
                                                .stream()
                                                .map(this::toFormField)
                                                .toList())
                                                .orElse(null));
                        })
                        .toList())
                .orElse(null);
    }

    private RetrievedApplicationFormField toFormField(Field formField) {
        Optional<Field> potentialFormField = Optional.ofNullable(formField);

        return new RetrievedApplicationFormField(potentialFormField.map(Field::getFieldId).orElse(null),
                                                 potentialFormField.map(Field::getFieldOptional).orElse(null),
                                                 potentialFormField.map(Field::getFieldPrivate).orElse(null),
                                                 potentialFormField.map(Field::getFieldVisible).orElse(null),
                                                 potentialFormField.map(f -> toLabelObject(f.getFieldTitle())).orElse(null),
                                                 potentialFormField.map(f -> f.getFieldType().value()).orElse(null));
    }

    private List<String> toPermissions(Application application) {
        Optional<Set<Application.ApplicationPermissionsEnum>> potentialPermissions = Optional.ofNullable(application.getApplicationPermissions());

        return potentialPermissions.map(permissions -> permissions
                        .stream()
                        .map(permission -> {
                            Optional<Application.ApplicationPermissionsEnum> potentialPermission = Optional.ofNullable(permission);

                            return potentialPermission.map(Application.ApplicationPermissionsEnum::value).orElse(null);
                        })
                        .toList())
                .orElse(null);
    }
    private List<RetrievedApplicationEvent> toEvents(Application application) {
        Optional<List<Event>> potentialEvents = Optional.ofNullable(application.getApplicationEvents());

        return potentialEvents.map(events -> events
                                .stream()
                                .map(event -> {
                                    Optional<Event> potentialEvent = Optional.ofNullable(event);
                                    return new RetrievedApplicationEvent(
                                            potentialEvent.map(e -> e.getEventActorAttributes().getUserid()).orElse(null),
                                            potentialEvent.map(Event::getEventTime).orElse(null),
                                            potentialEvent.map(Event::getEventType).orElse(null));
                                })
                        .toList())
                .orElse(null);
    }

    private List<RetrievedApplicationAttachment> toAttachments(Application application) {
        Optional<List<ApplicationAttachment>> potentialAttachments = Optional.ofNullable(application.getApplicationAttachments());

        return potentialAttachments.map(attachments -> attachments
                        .stream()
                        .map(attachment -> {
                            Optional<ApplicationAttachment> potentialAttachment = Optional.ofNullable(attachment);

                            return new RetrievedApplicationAttachment(potentialAttachment.map(ApplicationAttachment::getAttachmentId).orElse(null),
                                    potentialAttachment.map(ApplicationAttachment::getAttachmentFilename).orElse(null),
                                    potentialAttachment.map(ApplicationAttachment::getAttachmentType).orElse(null));
                        })
                        .toList())
                .orElse(null);
    }

    private List<RetrievedApplicationLicense> toLicences(Application application) {
        Optional<List<V2License>> potentialLicenses = Optional.ofNullable(application.getApplicationLicenses());

        return potentialLicenses.map(licenses -> licenses
                        .stream()
                        .map(license -> {
                            Optional<V2License> potentialLicense = Optional.ofNullable(license);

                            return new RetrievedApplicationLicense(potentialLicense.map(l -> l.getLicenseType().value()).orElse(null),
                                potentialLicense.map(l -> toLabelObject(l.getLicenseTitle())).orElse(null),
                                potentialLicense.map(V2License::getLicenseEnabled).orElse(null),
                                potentialLicense.map(V2License::getLicenseArchived).orElse(null));
                        })
                        .toList())
                .orElse(null);
    }

    private RetrievedApplication.StateEnum toState(Application application) {
        Optional<Application.ApplicationStateEnum> potentialState = Optional.ofNullable(application.getApplicationState());

        return potentialState.map(state -> RetrievedApplication.StateEnum.fromString(state.value())).orElse(null);
    }

    private List<Label> toLabelObject(Map<String, String> map) {
        return map
                .entrySet()
                .stream()
                .map(entry -> new Label(entry.getKey(), entry.getValue()))
                .toList();
    }
}

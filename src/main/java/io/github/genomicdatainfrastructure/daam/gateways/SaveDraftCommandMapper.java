// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.genomicdatainfrastructure.daam.model.SaveForm;
import io.github.genomicdatainfrastructure.daam.model.SaveFormField;
import io.github.genomicdatainfrastructure.daam.model.SaveFormsAndDuos;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SaveDraftCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SaveDraftCommandFieldValues;

public class SaveDraftCommandMapper {

    private SaveDraftCommandMapper() {
        // not used
    }

    public static SaveDraftCommand from(Long applicationId, SaveFormsAndDuos saveFormsAndDuos) {
        var forms = ofNullable(saveFormsAndDuos.getForms())
                .orElseGet(List::of);

        var fieldValues = forms.stream()
                .filter(Objects::nonNull)
                .flatMap(SaveDraftCommandMapper::parseFieldValues)
                .toList();

        return SaveDraftCommand.builder()
                .applicationId(applicationId)
                .fieldValues(fieldValues)
                .build();
    }

    private static Stream<SaveDraftCommandFieldValues> parseFieldValues(SaveForm form) {
        var fields = ofNullable(form.getFields())
                .orElseGet(List::of);

        return fields.stream()
                .filter(Objects::nonNull)
                .map(field -> parseFieldValue(form, field));
    }

    private static SaveDraftCommandFieldValues parseFieldValue(SaveForm form, SaveFormField field) {
        return SaveDraftCommandFieldValues.builder()
                .form(form.getFormId())
                .field(field.getFieldId())
                .value(field.getValue())
                .build();
    }
}

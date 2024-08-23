// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import io.github.genomicdatainfrastructure.daam.model.SaveForm;
import io.github.genomicdatainfrastructure.daam.model.SaveFormField;
import io.github.genomicdatainfrastructure.daam.model.SaveFormsAndDuos;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.FormTemplateTableValue;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SaveDraftCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SaveDraftCommandFieldValues;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

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
        Object value = field.getValue();
        if (Objects.nonNull(field.getTableValues())) {
            value = field.getTableValues().stream()
                    .filter(Objects::nonNull)
                    .filter(not(Collection::isEmpty))
                    .map(it -> it.stream()
                            .map(columnValue -> FormTemplateTableValue.builder()
                                    .column(columnValue.getColumn())
                                    .value(columnValue.getValue())
                                    .build()).toList()
                    )
                    .toList();
        }
        return SaveDraftCommandFieldValues.builder()
                .form(form.getFormId())
                .field(field.getFieldId())
                .value(value)
                .build();
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.gateways;

import io.github.genomicdatainfrastructure.daam.model.SaveForm;
import io.github.genomicdatainfrastructure.daam.model.SaveFormField;
import io.github.genomicdatainfrastructure.daam.model.SaveFormsAndDuos;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SaveDraftCommand;
import io.github.genomicdatainfrastructure.daam.remote.rems.model.SaveDraftCommandFieldValues;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SaveDraftCommandMapperTest {

    @Test
    void accepts_null_forms() {
        var applicationId = 1L;
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .forms(null)
                .build();

        var actual = SaveDraftCommandMapper.from(applicationId, saveFormsAndDuos);

        var expected = SaveDraftCommand.builder()
                .applicationId(applicationId)
                .fieldValues(List.of())
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void accepts_null_fields() {
        var applicationId = 1L;
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .forms(List.of(
                        SaveForm.builder()
                                .fields(null)
                                .build()
                ))
                .build();

        var actual = SaveDraftCommandMapper.from(applicationId, saveFormsAndDuos);

        var expected = SaveDraftCommand.builder()
                .applicationId(applicationId)
                .fieldValues(List.of())
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void can_parse() {
        var applicationId = 1L;
        var saveFormsAndDuos = SaveFormsAndDuos.builder()
                .forms(List.of(
                        SaveForm.builder()
                                .formId(1L)
                                .fields(List.of(
                                        SaveFormField.builder()
                                                .fieldId("fieldId")
                                                .value("value")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        var actual = SaveDraftCommandMapper.from(applicationId, saveFormsAndDuos);

        var expected = SaveDraftCommand.builder()
                .applicationId(applicationId)
                .fieldValues(List.of(
                        SaveDraftCommandFieldValues.builder()
                                .form(1L)
                                .field("fieldId")
                                .value("value")
                                .build()
                ))
                .build();

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}

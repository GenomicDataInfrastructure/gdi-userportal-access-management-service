// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

import io.github.genomicdatainfrastructure.daam.model.ValidationWarning;
import lombok.Getter;

import java.util.List;

import static java.util.Optional.ofNullable;

@Getter
public class ApplicationSubmissionException extends RuntimeException {

    private static final String MESSAGE = "Application %s could not be submitted.";
    private final transient List<ValidationWarning> warnings;

    public ApplicationSubmissionException(Long applicationId, List<ValidationWarning> warnings) {
        super(String.format(MESSAGE, applicationId));
        this.warnings = ofNullable(warnings).orElseGet(List::of);
    }
}

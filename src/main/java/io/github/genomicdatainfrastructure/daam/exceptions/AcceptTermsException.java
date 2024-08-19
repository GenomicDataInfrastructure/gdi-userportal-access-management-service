// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.exceptions;

import io.github.genomicdatainfrastructure.daam.model.ValidationWarning;
import lombok.Getter;

import java.util.List;

import static java.util.Optional.ofNullable;

@Getter
public class AcceptTermsException extends RuntimeException {

    private static final String MESSAGE = "Terms and conditions could not be accepted.";
    private final transient List<ValidationWarning> warnings;

    public AcceptTermsException(List<ValidationWarning> warnings) {
        super(MESSAGE);
        this.warnings = ofNullable(warnings).orElseGet(List::of);
    }
}

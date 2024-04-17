// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

import java.util.List;

public class ApplicationSubmissionException extends RuntimeException {

    private static final String MESSAGE = "Application %s could not be submitted";
    private final List<String> errorMessages;

    public ApplicationSubmissionException(Long applicationId, List<String> errorMessages) {
        super(String.format(MESSAGE, applicationId) + (errorMessages == null || errorMessages
                .isEmpty() ? "" : ": " + String.join(", ", errorMessages)));
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
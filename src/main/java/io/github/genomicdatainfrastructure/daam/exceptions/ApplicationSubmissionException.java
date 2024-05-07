// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

import java.util.List;

public class ApplicationSubmissionException extends RuntimeException {

    private static final String MESSAGE = "Application %s could not be submitted due to the following errors:";
    private final List<String> errorMessages;

    public ApplicationSubmissionException(Long applicationId, List<String> errorMessages) {
        super(String.format(MESSAGE, applicationId, String.join("\n", errorMessages)));
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
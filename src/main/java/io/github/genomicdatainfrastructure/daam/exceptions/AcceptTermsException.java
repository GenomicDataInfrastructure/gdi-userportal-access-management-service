// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.exceptions;

import java.util.List;

public class AcceptTermsException extends RuntimeException {

    private static final String MESSAGE = "Could not be accept terms for application %s due to the following errors:";
    private final List<String> errorMessages;

    public AcceptTermsException(Long applicationId, List<String> errorMessages) {
        super(String.format(MESSAGE, applicationId));
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}

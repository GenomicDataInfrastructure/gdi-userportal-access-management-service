// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotInCorrectStateException extends RuntimeException {

    private static final String MESSAGE = "The application is not in correct state: %s.";

    public ApplicationNotInCorrectStateException(String state) {
        super(MESSAGE.formatted(state));
    }
}

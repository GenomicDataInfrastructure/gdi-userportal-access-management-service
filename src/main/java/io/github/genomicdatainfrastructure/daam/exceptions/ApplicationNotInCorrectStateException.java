// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotInCorrectStateException extends RuntimeException {

    private static final String MESSAGE = "Application %s is not in correct state: %s";
    public ApplicationNotInCorrectStateException(Long id, String state) {
        super(MESSAGE.formatted(id, state));
    }
}
// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Application %s not found";
    public ApplicationNotFoundException(Long applicationId) {
        super(MESSAGE.formatted(applicationId));
    }
}
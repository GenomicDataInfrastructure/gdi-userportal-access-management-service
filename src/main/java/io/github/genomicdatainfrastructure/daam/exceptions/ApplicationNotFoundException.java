// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotFoundException extends RuntimeException {

    private static final String MESSAGE = "The application was not found.";

    public ApplicationNotFoundException() {
        super(MESSAGE);
    }
}

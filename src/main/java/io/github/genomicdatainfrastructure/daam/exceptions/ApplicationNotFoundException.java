// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException() {
        super("The application was not found.");
    }

    public ApplicationNotFoundException(Long id) {
        super("The application with Id %s was not found.".formatted(id));
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotInCorrectStateException extends RuntimeException {
    public ApplicationNotInCorrectStateException(Long id) {
        super("Application " + id + " is not in a correct state to be submitted");
    }
}
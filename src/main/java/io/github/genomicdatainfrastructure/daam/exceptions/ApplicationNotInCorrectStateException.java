// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class ApplicationNotInCorrectStateException extends RuntimeException {
    public ApplicationNotInCorrectStateException(String message) {
        super(message);
    }
}
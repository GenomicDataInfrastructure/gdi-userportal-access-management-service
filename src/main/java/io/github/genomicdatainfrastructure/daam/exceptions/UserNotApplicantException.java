// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class UserNotApplicantException extends RuntimeException {
    public UserNotApplicantException(String message) {
        super(message);
    }
}
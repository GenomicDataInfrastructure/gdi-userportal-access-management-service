// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class UserNotApplicantException extends RuntimeException {

    private static final String MESSAGE = "The user %s cannot add to application %s";

    public UserNotApplicantException(String username, Long applicationId) {
        super(MESSAGE.formatted(username, applicationId));
    }
}

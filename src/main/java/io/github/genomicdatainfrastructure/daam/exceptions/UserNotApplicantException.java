// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class UserNotApplicantException extends RuntimeException {

    private static final String MESSAGE = "The user %s is not an applicant.";

    public UserNotApplicantException(String userId) {
        super(MESSAGE.formatted(userId));
    }
}

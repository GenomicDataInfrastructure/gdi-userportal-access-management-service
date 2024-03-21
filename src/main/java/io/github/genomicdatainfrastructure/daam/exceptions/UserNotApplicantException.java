// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class UserNotApplicantException extends RuntimeException {

    private static final String MESSAGE = "User %s is not an applicant for application %s";
    public UserNotApplicantException(Long applicationId, String userId) {
        super(MESSAGE.formatted(userId, applicationId));
    }
}
// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class UserNotApplicantException extends RuntimeException {
    public UserNotApplicantException(Long applicationId, String userId) {
        super("User " + userId + " is not an applicant for application " + applicationId);
    }
}
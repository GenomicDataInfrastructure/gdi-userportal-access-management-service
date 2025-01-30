// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.exceptions;

public class MemberNotInvitedException extends RuntimeException {

    public MemberNotInvitedException(String name) {
        super("Member %s could not be invited".formatted(name));
    }
}

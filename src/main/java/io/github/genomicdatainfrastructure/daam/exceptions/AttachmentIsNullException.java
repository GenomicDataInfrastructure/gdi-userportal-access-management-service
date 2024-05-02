// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.exceptions;

public class AttachmentIsNullException extends RuntimeException {

    private static final String MESSAGE = "Null file is not accepted.";

    public AttachmentIsNullException() {
        super(MESSAGE);
    }
}

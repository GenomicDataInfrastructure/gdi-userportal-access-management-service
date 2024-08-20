// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.exceptions;

public class AttachmentTooLargeException extends RuntimeException {

    private static final String MESSAGE = "The file %s is too big.";

    public AttachmentTooLargeException(String filename) {
        super(MESSAGE.formatted(filename));
    }
}

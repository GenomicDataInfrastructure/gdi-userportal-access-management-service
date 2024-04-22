// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.exceptions;

import static java.lang.String.format;

public class AttachmentTooLargeException extends RuntimeException {

    private static final String MESSAGE = "File %s is too big.";

    public AttachmentTooLargeException(String filename) {
        super(format(MESSAGE, filename));
    }
}

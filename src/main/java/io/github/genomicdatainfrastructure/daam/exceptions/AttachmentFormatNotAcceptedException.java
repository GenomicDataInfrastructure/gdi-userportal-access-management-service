// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.exceptions;

import static java.lang.String.format;

public class AttachmentFormatNotAcceptedException extends RuntimeException {

    private static final String MESSAGE = "Format of file %s is not accepted";

    public AttachmentFormatNotAcceptedException(String filename) {
        super(format(MESSAGE, filename));
    }
}

// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class CatalogueItemNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Catalogue Item not found for dataset ID: %s";

    public CatalogueItemNotFoundException(String datasetId) {
        super(MESSAGE.formatted(datasetId));
    }
}

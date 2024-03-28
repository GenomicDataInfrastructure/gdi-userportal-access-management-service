// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0
package io.github.genomicdatainfrastructure.daam.exceptions;

public class CatalogueItemNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Unable to find a REMS Catalogue Item for the given CKAN Dataset ID: %s.";

    public CatalogueItemNotFoundException(String datasetId) {
        super(MESSAGE.formatted(datasetId));
    }
}

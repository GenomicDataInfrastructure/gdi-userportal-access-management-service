// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.mappers;

import io.github.genomicdatainfrastructure.daam.model.RetrievedApplication;

public interface ApplicationMapper<T> {

    public RetrievedApplication from(T application);
}

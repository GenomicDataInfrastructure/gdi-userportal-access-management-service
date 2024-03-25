// SPDX-FileCopyrightText: 2024 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.services;

import java.io.File;

import io.github.genomicdatainfrastructure.daam.model.AddedAttachment;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AttachFileToApplicationService {

    public AddedAttachment attach(Long applicationId, File file) {
        throw new UnsupportedOperationException("Unimplemented method 'attach'");
    }
}

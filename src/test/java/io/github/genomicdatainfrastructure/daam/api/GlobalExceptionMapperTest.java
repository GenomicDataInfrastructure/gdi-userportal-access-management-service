// SPDX-FileCopyrightText: 2026 PNED G.I.E.
//
// SPDX-License-Identifier: Apache-2.0

package io.github.genomicdatainfrastructure.daam.api;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.genomicdatainfrastructure.daam.model.ErrorResponse;

class GlobalExceptionMapperTest {

    @Test
    void does_not_expose_exception_details() {
        try (var response = new GlobalExceptionMapper().toResponse(new IOException(
                "/private/tmp/secret.pdf"))) {
            assertThat(response.getStatus()).isEqualTo(500);
            var error = (ErrorResponse) response.getEntity();
            assertThat(error.getDetail()).isEqualTo(
                    "The request could not be completed. Check its outcome or contact support before trying again.");
        }
    }
}

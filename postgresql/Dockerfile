# SPDX-FileCopyrightText: 2006-2023 Open Knowledge Foundation and contributors
#
# SPDX-License-Identifier: Apache-2.0

FROM postgres:12-alpine

# Include extra setup scripts (eg datastore)
COPY --chown=postgres:postgres docker-entrypoint-initdb.d /docker-entrypoint-initdb.d

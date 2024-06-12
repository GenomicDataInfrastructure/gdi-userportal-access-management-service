<!--
SPDX-FileCopyrightText: 2024 PNED G.I.E.

SPDX-License-Identifier: CC-BY-4.0
-->

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [v1.2.0] - 2024-06-12

### Added

- feat: #7 bootstrap quarkus app by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/17
- feat: #16 add authentication by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/18
- feat: implement create application endpoint by @EmiPali in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/19
- doc: #10 add samples of http requests to REMS by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/15
- doc: #9 add apidocs by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/14
- feat: implement submit application endpoint by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/22
- feat: Only submit applications that are in draft or returned status by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/24
- feat: Move fetching of userId in API layer by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/26
- feat: application details endpoint by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/25
- feat: #13 implement endpoint to attach single file by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/33
- feat: #13 check if not submitted before attachment by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/34
- feat: #12 add endpoint to save application by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/36
- feat: endpoint to get catalogue item by resource id by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/37
- feat: get catalogue-item-id from dataset-id and use that to create application by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/38
- feat: Return datasets along with application in ListedApplication by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/42
- feat: Send description and createdAt by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/43
- feat: return external id in application dataset by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/45
- Correct field schema by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/47
- feat: Return applicationId after creation of application by @inderps in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/48
- feat: #44 handling exception when success value is false by @EmiPali in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/49
- feat: continiously deployment to azure by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/51
- feat: return 400 when attachment file is null by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/52
- feat: run quarkus migration and adjust pom by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/60
- feat: implement endpoint to retrieve granted dataset identifiers by @EmiPali in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/68
- fixture for body specified in rems.yaml by @EmiPali in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/69

### Changed

- chore: Add debug level to test bug on dev env by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/61
- chore:upgrade redhat image by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/62
- chore(ci): enable test coverage by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/27
- chore: fix checkstyle configuration in CI/CD by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/23
- chore: clean code by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/29
- chore: review open api files by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/30
- chore: split tests by use case by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/32
- chore: #12 enhance REMS openapi docs by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/35
- chore: point examples to gdi-userportal-deployment by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/39
- chore: improve code by @admy7 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/31

### Deprecated

### Removed

### Fixed

- fix(azure-deployment): Trigger repull on new ams version by updating … by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/20
- fix: Order of metadata tags to ensure correct tag is used by @hcvdwerf in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/21
- fix: review rems openapi doc by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/40
- fix: move file to temp before uploading to REMS by @brunopacheco1 in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/41
- fix: fix app properties by @EmiPali in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/57
- fix: structuring ErrorMessages before sending them to frontend by @EmiPali in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/59
- fix: remove trivy high level by @sulejmank in https://github.com/GenomicDataInfrastructure/gdi-userportal-access-management-service/pull/65

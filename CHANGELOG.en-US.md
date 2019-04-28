# Changelog
All notable changes about issue-service will be documented in this file.

## [0.16.0] - 2019-04-19

### Added

- Added 'feature' issueType 
- Added program issueType scheme
- Added program stateMachine scheme

## [0.13.0] - 2019-01-11

### Changed

- Delete useless code logic and related tables.

### Fixed

- State-machine scheme solution search.
- After state-machine scheme deployed, removed the state-machine is still active.


## [0.12.0] - 2018-12-14

### Added

- State-machine scheme add deploy status.
- State-machine scheme add draft configuration.
- Support state-machine scheme configuration changes, and deploy affect the issues.
- State-machine scheme deployed add synchronous control.
- The test project's default issue-type scheme add new issue-type: auto-test.
- Added all the unit tests.

### Fixed

- Repair the old issue-type scheme of auto-test configuration.
- Repair supplement deploy/draft configuration state-machine scheme.


## [0.11.0] - 2018-11-16

### Added

- Organization level add priority maintenance.
- Organization level add issue-type maintenance.
- Added issue-type scheme display.
- Added state-machine scheme display.
- Each combination scheme configuration applied to the project level.

### Fixed

- Migration on the priority of agile/test project to the Organization level.
- Migration on the issue-type of agile/test project to the Organization level.
# Change Log
All notable changes to this project will be documented in this file. [All Releases](https://github.com/bitium/confluence-saml-plugin/releases)

## [Unreleased]
### Changed
- Fix for Force SSO used together with Secure Administration Sessions
- Improved logging for SAML errors

## [1.0.4] - 2016-02-18
### Added
- Refactored the logic for creating a session for SSO user
- Can now auto-create users and add to a specific user group. The group used can be configured in the plugin config.
- Refactored SAMLConfig, SsoLoginServlet and SsoLogoutServlet logic into the common [atlassian-saml](https://github.com/bitium/atlassian-saml) project
- Plugin now redirects to the correct requested page after SSO. No longer defaulting to the dashboard.

## [1.0.1] - 2015-07-01
### Added
- Initial Release

[unreleased]: https://github.com/bitium/confluence-saml-plugin/tree/develop
[1.0.4]: https://github.com/bitium/confluence-saml-plugin/releases/tag/v1.0.4
[1.0.1]: https://github.com/bitium/confluence-saml-plugin/releases/tag/v1.0.1
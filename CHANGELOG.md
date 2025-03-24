# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial implementation of ProxyCheck.io API client
- Support for checking single IP addresses
- Support for checking multiple IP addresses
- Support for checking email addresses
- Support for getting dashboard information
- Support for all query flags and parameters
- Proper error handling with custom exceptions
- Rate limit handling
- Caching support
- Asynchronous API support using Kotlin Coroutines

### Changed
- Introduced `ProxyCheckApiInterface` and adapter implementations (`ProxyCheckApiClientAdapter`, `ProxyCheckApiAdapter`) for better flexibility and testability
- Improved API with `ProxyCheckOptions` class for configuring API requests, replacing individual boolean parameters
- Added builder pattern for `ProxyCheckOptions` with fluent API
- Enhanced type safety with enum types for status (`ResponseStatus`), proxy status (`ProxyStatus`), and proxy type (`ProxyType`)
- Added specific flag enums (`VpnFlag`, `AsnFlag`, `NodeFlag`, etc.) for more type-safe configuration
- Updated examples to demonstrate the new API patterns and enum-based conditional logic
- Improved documentation with more comprehensive examples
- Refactored exception handling to reduce redundancy by introducing an `ExceptionHandler` utility class

### Deprecated
- Direct use of boolean parameters in API methods (use `ProxyCheckOptions` instead)

### Removed

### Fixed
- Fixed issues with setting both flags and individual boolean parameters by using `ProxyCheckOptions`

### Security

## [1.0.0] - TBD

Initial release

# Contributing to ProxyCheck.io API Client

Thank you for considering contributing to the ProxyCheck.io API Client! This document provides guidelines and instructions for contributing to this project.

## Code of Conduct

Please be respectful and considerate of others when contributing to this project. We aim to foster an inclusive and welcoming community.

## How to Contribute

There are many ways to contribute to this project:

1. Reporting bugs
2. Suggesting enhancements
3. Writing documentation
4. Submitting code changes
5. Reviewing pull requests

## Development Setup

1. Fork the repository
2. Clone your fork: `git clone https://github.com/your-username/ProxycheckIOApi.git`
3. Create a branch for your changes: `git checkout -b feature/your-feature-name`
4. Make your changes
5. Run tests (once they are implemented): `./gradlew test`
6. Commit your changes: `git commit -m "Add your feature description"`
7. Push to your fork: `git push origin feature/your-feature-name`
8. Create a pull request

## Pull Request Process

1. Ensure your code follows the project's coding style
2. Update the README.md with details of changes if applicable
3. Add tests for your changes if applicable
4. Make sure all tests pass
5. Your pull request will be reviewed by the maintainers

## Coding Guidelines

- Follow Kotlin coding conventions
- Write clear, concise, and descriptive code
- Document your code with KDoc comments
- Write unit tests for your code

## Testing

When adding new features or fixing bugs, please add tests to verify your changes. We use JUnit 5 for testing.

### Test Structure

Tests should be organized as follows:

1. **Unit Tests**: Test individual components in isolation
   - Place in the same package as the class being tested
   - Name the test class as `[ClassUnderTest]Test`
   - Use mocks for dependencies when appropriate

2. **Integration Tests**: Test interactions between components
   - Place in a separate package with `.integration` suffix
   - Focus on testing the integration points between components

3. **API Tests**: Test the public API of the library
   - Place in a separate package with `.api` suffix
   - Test the behavior of the API from a client perspective

### Test Guidelines

- Each test method should test a single behavior
- Use descriptive test method names that explain what is being tested
- Follow the Arrange-Act-Assert pattern
- Add comments to explain complex test setups
- Use parameterized tests for testing multiple inputs
- Mock external dependencies (like the ProxyCheck.io API) for unit tests

### Running Tests

```bash
./gradlew test
```

## Reporting Bugs

Please report bugs by opening an issue on the [GitHub Issues page](https://github.com/SquareCodeFX/ProxycheckIOApi/issues).

When reporting bugs, please include:

1. A clear and descriptive title
2. Steps to reproduce the bug
3. Expected behavior
4. Actual behavior
5. Screenshots if applicable
6. Your environment (OS, Java version, etc.)

## Branching Strategy

We follow a simplified Git flow branching strategy:

1. `main` - The main branch containing stable, released code
2. `develop` - The development branch for integrating features
3. `feature/*` - Feature branches for new features or enhancements
4. `bugfix/*` - Bugfix branches for fixing issues
5. `release/*` - Release branches for preparing releases

### Branch Naming

- Feature branches: `feature/short-description`
- Bugfix branches: `bugfix/issue-number-short-description`
- Release branches: `release/version-number`

### Workflow

1. Create a new branch from `develop` for your feature or bugfix
2. Make your changes and commit them
3. Push your branch to your fork
4. Create a pull request to the `develop` branch
5. After review and approval, your changes will be merged

## Code Review Process

All code changes require a code review before being merged. Here's what reviewers will look for:

1. **Functionality**: Does the code work as expected?
2. **Code Quality**: Is the code well-written, maintainable, and follows best practices?
3. **Documentation**: Are the changes properly documented with KDoc comments?
4. **Tests**: Are there appropriate tests for the changes?
5. **Performance**: Are there any performance concerns with the changes?
6. **Security**: Are there any security concerns with the changes?

### Review Guidelines

- Be respectful and constructive in your feedback
- Focus on the code, not the person
- Explain why you're suggesting changes
- Provide examples or references when appropriate

## Suggesting Enhancements

Please suggest enhancements by opening an issue on the [GitHub Issues page](https://github.com/SquareCodeFX/ProxycheckIOApi/issues) with the "enhancement" label.

When suggesting enhancements, please include:

1. A clear and descriptive title
2. A detailed description of the enhancement
3. Why this enhancement would be useful
4. Any examples or mockups if applicable

## License

By contributing to this project, you agree that your contributions will be licensed under the project's MIT License.

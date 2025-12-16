# Contributing to SaveLife Patient Management System

Thank you for your interest in contributing to the SaveLife Patient Management System! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Respect different viewpoints and experiences

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone git@github.com:your-username/SaveLife-patient-managment-system.git`
3. Create a feature branch: `git checkout -b feature/your-feature-name`
4. Make your changes
5. Commit your changes: `git commit -m "Add feature: description"`
6. Push to your fork: `git push origin feature/your-feature-name`
7. Create a Pull Request

## Development Guidelines

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Maintain consistent formatting (4 spaces for indentation)
- Keep methods focused and small (< 50 lines when possible)

### Commit Messages

Follow the conventional commit format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

Example:
```
feat(patient-service): Add patient search functionality

- Implement search by name and email
- Add pagination support
- Update API documentation

Closes #123
```

### Testing

- Write unit tests for business logic
- Include integration tests for API endpoints
- Maintain minimum 70% code coverage
- All tests must pass before submitting PR

### Pull Request Process

1. Update README.md if needed
2. Update DEPLOYMENT.md if deployment changes are made
3. Ensure all tests pass
4. Ensure code follows style guidelines
5. Request review from maintainers
6. Address review comments
7. Once approved, maintainers will merge

## Project Structure

```
java-spring-microservices/
├── api-gateway/          # API Gateway service
├── auth-service/         # Authentication service
├── patient-service/      # Patient management service
├── billing-service/      # Billing service (gRPC)
├── analytics-service/    # Analytics and event processing
├── infrastructure/       # AWS CDK infrastructure code
├── integration-tests/    # End-to-end integration tests
├── k8s/                  # Kubernetes manifests
├── api-requests/         # HTTP request examples
└── grpc-requests/        # gRPC request examples
```

## Adding a New Feature

1. Create a feature branch from `main`
2. Implement the feature with tests
3. Update documentation if needed
4. Ensure all tests pass
5. Submit a Pull Request

## Reporting Bugs

Use GitHub Issues to report bugs. Include:

- Description of the bug
- Steps to reproduce
- Expected behavior
- Actual behavior
- Environment details (OS, Java version, etc.)
- Screenshots if applicable

## Requesting Features

Use GitHub Issues to request features. Include:

- Description of the feature
- Use case and motivation
- Proposed implementation (if any)
- Alternatives considered

## Questions?

Feel free to open an issue for questions or reach out to the maintainers.

Thank you for contributing!


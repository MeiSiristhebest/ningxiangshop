# Contributing to 宁享购 (Ningxiang Go)

We love your input! We want to make contributing to 宁享购 as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

## Code of Conduct

Please be respectful and considerate of others when contributing to this project. We aim to foster an inclusive and welcoming community.

## Development Process

We use GitHub to host code, to track issues and feature requests, as well as accept pull requests.

### Setting Up the Development Environment

**Prerequisites**:
- JDK 21 LTS
- Maven 3.9+
- MySQL 8.0+
- Redis 7+
- Elasticsearch 8+
- Node.js 18+ (for the front-end module)

**Setup steps**:

1. Fork the repository
2. Clone your fork: `git clone https://github.com/yourusername/ningxiangshop.git`
3. Set up infrastructure (MySQL, Redis, Elasticsearch, etc.)
4. Configure database connection in `application.yml` or via environment variables
5. Build the project: `mvn clean package -DskipTests`
6. Start services according to the module startup order (gateway last)

### Branch Naming Convention

- `feature/feature-name`: For new features
- `fix/issue-description`: For bug fixes
- `docs/documentation-update`: For documentation updates
- `refactor/module-name`: For code refactoring
- `perf/description`: For performance-related improvements

### Commit Message Convention

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification for commit messages:

- `feat:` A new feature
- `fix:` A bug fix
- `docs:` Documentation only changes
- `style:` Changes that do not affect the meaning of the code (formatting, etc)
- `refactor:` A code change that neither fixes a bug nor adds a feature
- `perf:` A code change that improves performance
- `test:` Adding missing tests or correcting existing tests
- `chore:` Changes to the build process or auxiliary tools

### Pull Request Process

1. Create a new branch from `master` following the branch naming convention
2. Make your changes
3. Ensure the project compiles: `mvn clean compile`
4. Run tests and ensure they pass: `mvn test`
5. Update documentation if necessary
6. Create a pull request to the `master` branch
7. Wait for review and address any feedback

## Any contributions you make will be under the GNU AGPL-3.0 License

In short, when you submit github contributions, you're agreeing to license them under the same terms as the project's license (GNU AGPL-3.0).

## Report bugs using GitHub's issue tracker

We use GitHub issues to track public bugs. Report a bug by opening a new issue; it's that easy!

## Write bug reports with detail, background, and sample code

**Great Bug Reports** tend to have:

- A quick summary and/or background
- Steps to reproduce
  - Be specific!
  - Give sample code if you can.
- What you expected would happen
- What actually happens
- Notes (possibly including why you think this might be happening, or stuff you tried that didn't work)

## Module Guidelines

When modifying microservice modules, please keep them independent and follow the single responsibility principle. For cross-module changes, update the common module (ningxiang-common) and ensure all affected modules compile.

## Documentation

- Update README.md with any new features or changes
- Document new API endpoints and RPC interfaces
- Keep database migration scripts in `db/`

## Questions?

If you have any questions or need help, please open an issue or reach out to the maintainers.

Thank you for contributing!

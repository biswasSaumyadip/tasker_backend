# Tasker Application

## Development Setup

### Prerequisites
- JDK 21
- Git
- Gradle (wrapper included)

### First-time Setup
1. Clone the repository
2. Run any of these commands to set up the project (they will automatically set up git hooks):
   ```bash
   # Option 1: Using Gradle (recommended)
   ./gradlew build

   # Option 2: Direct script (if needed)
   ./setup-hooks.bat
   ```

### Development Workflow

#### Code Style and Quality
This project uses several tools to maintain code quality:

1. **Spotless** - Code formatting
   - Automatically formats code on commit
   - Manual format: `./gradlew spotlessApply`

2. **Checkstyle** - Code style checks
   - Runs on commit
   - Manual check: `./gradlew checkstyleMain checkstyleTest`

3. **JaCoCo** - Code coverage
   - Requires 80% test coverage
   - View reports: `build/reports/jacoco/test/html/index.html`

4. **Git Hooks**
   - Pre-commit hook runs:
     - Code formatting
     - Checkstyle
     - Tests
     - Coverage verification

#### Useful Gradle Tasks
- `./gradlew build` - Full build with tests
- `./gradlew test` - Run tests
- `./gradlew dependencyUpdates` - Check for dependency updates
- `./gradlew sonarqube` - Run SonarQube analysis (requires configuration)

### Best Practices
1. Write tests for new code
2. Keep test coverage above 80%
3. Follow code style guidelines
4. Update dependencies regularly
5. Document significant changes

### IDE Setup
This project includes:
- `.editorconfig` - Consistent coding style across IDEs
- Checkstyle configuration
- Spotless formatting rules

Configure your IDE to:
1. Use EditorConfig
2. Format on save
3. Enable Checkstyle plugin

### Commit Message Guidelines

We follow the Conventional Commits specification. Each commit message should be structured as follows:

```
<type>(<scope>): <subject>

[optional body]
```

#### Types
- build: Changes that affect the build system or external dependencies
- chore: Regular maintenance tasks
- ci: Changes to CI configuration files and scripts
- docs: Documentation only changes
- feat: A new feature
- fix: A bug fix
- perf: A code change that improves performance
- refactor: A code change that neither fixes a bug nor adds a feature
- revert: Reverting a previous commit
- style: Changes that do not affect the meaning of the code
- test: Adding missing tests or correcting existing tests

#### Rules
- Type must be one of the types listed above
- Subject must start with a capital letter
- No period at the end of the subject
- Maximum line length is 100 characters
- Use imperative mood in the subject line

#### Examples
```
feat: Add user authentication system
fix(database): Resolve connection pooling issue
docs: Update API documentation with new endpoints
```

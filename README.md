# DynamicJsonMapper

`DynamicJsonMapper` is a lightweight Java utility for rendering JSON responses from template files using dynamic placeholder data.

## Why this project
- Keep API response shapes in JSON template files.
- Replace placeholder tokens (for example `${name}`, `${city}`) at runtime.
- Link parent and child templates for object/array rendering (via `$template_ref` and `$source`).
- Support nested objects and arrays with recursive traversal.
- Reuse parsed templates via in-memory cache for better performance.

## Project structure
- `src/main/java/com/thanhlv/DynamicJsonMapper/`: Java source code (`JsonTemplateMapper`, `DynamicJsonMapper`, `DeepNestedMapper`, `PaginatedListMapper`)
- `src/main/resources/templates/`: JSON templates (`user-template.json`, `deep-nested-template.json`, `paginated-users-template.json`, `paginated-user-item-template.json`, `tags-template.json`, `tag-item-template.json`)
- `src/test/java/com/thanhlv/DynamicJsonMapper/`: JUnit test suites (`JsonTemplateMapperTest`, `PaginatedListMapperTest`)
- `src/test/resources/templates/`: test fixtures for template-driven scenarios
- `docs/FEATURES.md`: detailed, always-updated feature list
- `AGENTS.md`: contributor and AI collaboration guidelines

## Tech stack
- Java (Gradle project)
- Jackson Databind `3.1.3`
- JUnit 5

## Build and test
```bash
./gradlew clean build
./gradlew test
./gradlew classes
```

## Publish to Maven Central (GitHub Actions)
- Workflow file: `.github/workflows/publish-central.yml`.
- Trigger methods:
- Push tag release: `v*` (example: `v1.2.3`).
- Manual run via GitHub Actions (`workflow_dispatch`) with `release_version`.
- Required GitHub repository secrets:
- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `MAVEN_GPG_PRIVATE_KEY` (ASCII-armored private key)
- `MAVEN_GPG_PASSPHRASE`
- Build uses Gradle task: `publishToMavenCentral` with `-PreleaseVersion=...`.

## Publish to Maven Central (Local Script)
- Script file: `scripts/publish-central.sh`.
- Env template file: `scripts/publish-central.env.example`.
- Usage:
```bash
cp scripts/publish-central.env.example scripts/publish-central.env
vi scripts/publish-central.env
source scripts/publish-central.env
./scripts/publish-central.sh 1.2.3
```
- Required environment variables (one of each pair is enough):
- `ORG_GRADLE_PROJECT_mavenCentralUsername` or `MAVEN_CENTRAL_USERNAME`
- `ORG_GRADLE_PROJECT_mavenCentralPassword` or `MAVEN_CENTRAL_PASSWORD`
- `ORG_GRADLE_PROJECT_signingInMemoryKey` or `MAVEN_GPG_PRIVATE_KEY`
- `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword` or `MAVEN_GPG_PASSPHRASE`

## Run demos
Run these main classes from your IDE:
- `com.thanhlv.DynamicJsonMapper.DynamicJsonMapperTest`
- `com.thanhlv.DynamicJsonMapper.DeepNestedMapperTest`
- `com.thanhlv.DynamicJsonMapper.PaginatedListMapperTest`

## Core usage
```java
JsonNode output = JsonTemplateMapper.render("templates/user-template.json", rawData);
String pretty = JsonTemplateMapper.jsonNodeToString(output);
```

## Contributing
- Keep commits focused and descriptive (`type(scope): summary`).
- Update `docs/FEATURES.md` in the same change whenever behavior or templates are modified.

## AI Maintenance Skills
- `skills/code-doc-sync`: reconcile docs and implementation (`docs-first` or `code-first`).
- `skills/code-test-sync`: keep implementation and JUnit tests synchronized when behavior changes.

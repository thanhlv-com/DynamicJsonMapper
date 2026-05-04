# DynamicJsonMapper

`DynamicJsonMapper` is a lightweight Java utility for rendering JSON responses from template files using dynamic placeholder data.

## Why this project
- Keep API response shapes in JSON template files.
- Replace placeholder tokens (for example `${name}`, `${city}`) at runtime.
- Support nested objects and arrays with recursive traversal.
- Reuse parsed templates via in-memory cache for better performance.

## Project structure
- `src/main/java/com/thanhlv/DynamicJsonMapper/`: Java source code (`JsonTemplateMapper`, `DynamicJsonMapper`, `DeepNestedMapper`, `PaginatedListMapper`)
- `src/main/resources/templates/`: JSON templates (`user-template.json`, `deep-nested-template.json`, `paginated-users-template.json`)
- `docs/FEATURES.md`: detailed, always-updated feature list
- `AGENTS.md`: contributor and AI collaboration guidelines

## Tech stack
- Java (Gradle project)
- Jackson Databind `3.1.3`
- JUnit 5 (configured, add tests under `src/test/java`)

## Build and test
```bash
./gradlew clean build
./gradlew test
./gradlew classes
```

## Run demos
Run these main classes from your IDE:
- `com.thanhlv.DynamicJsonMapper.DynamicJsonMapper`
- `com.thanhlv.DynamicJsonMapper.DeepNestedMapper`
- `com.thanhlv.DynamicJsonMapper.PaginatedListMapper`

## Core usage
```java
JsonNode output = JsonTemplateMapper.render("templates/user-template.json", rawData);
String pretty = JsonTemplateMapper.jsonNodeToString(output);
```

## Contributing
- Keep commits focused and descriptive (`type(scope): summary`).
- Update `docs/FEATURES.md` in the same change whenever behavior or templates are modified.

## AI Maintenance Skill
- Skill location: `skills/code-doc-sync`.
- Use it to keep code and documentation synchronized after changes.
- Priority options:
- `docs-first`: update implementation to match docs.
- `code-first`: update docs to match implementation.
- Skill location: `skills/code-test-sync`.
- Use it to keep implementation and JUnit tests synchronized; when coverage is missing, add/update tests in the same change.

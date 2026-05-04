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

## AI Maintenance Skills
- `skills/code-doc-sync`: reconcile docs and implementation (`docs-first` or `code-first`).
- `skills/code-test-sync`: keep implementation and JUnit tests synchronized when behavior changes.

# DynamicJsonMapper

`DynamicJsonMapper` is a lightweight Java library for rendering JSON responses from template files using dynamic placeholder data.

## Why this project
- Keep API response shapes in JSON template files.
- Replace placeholder tokens (for example `${name}`, `${city}`) at runtime.
- Link parent and child templates for object/array rendering (via `$template_ref` and `$source`).
- Support nested objects and arrays with recursive traversal.
- Reuse parsed templates via in-memory cache for better performance.

## Project structure
- `src/main/java/com/thanhlv/dynamicjsonmapper/`: Java source code (`JsonTemplateMapper`, `TemplateRenderer`, `TemplateReferenceResolver`, ...)
- `src/test/resources/templates/`: test fixtures for template-driven scenarios
- `docs/FEATURES.md`: detailed, always-updated feature list
- `AGENTS.md`: contributor and AI collaboration guidelines

## Tech stack
- Java (Gradle)
- Jackson Databind `3.1.3`
- JUnit 5

## Build and test
```bash
./gradlew clean build
./gradlew test
./gradlew classes
```

## Build as library
```bash
./gradlew jar
./gradlew publishToMavenLocal
```

Maven coordinates:
- `groupId`: `com.thanhlv.dynamicjsonmapper`
- `artifactId`: `dynamic-json-mapper`
- `version`: `1.0.0`

## Publish to Maven Central (GitHub Actions)
- Workflow file: `.github/workflows/publish-maven-central.yml`
- Trigger:
- `workflow_dispatch`
- GitHub Release `published`
- `push` tag matching `v*` (for example: `v1.0.1`)
- Validation command: `./gradlew clean test`
- Publish command: `./gradlew publish closeAndReleaseSonatypeStagingRepository`

Required GitHub repository secrets:
- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `MAVEN_CENTRAL_GPG_PRIVATE_KEY` (ASCII-armored private key)
- `MAVEN_CENTRAL_GPG_PASSPHRASE`

Required Maven Central endpoint (configured in `build.gradle`):
- `https://ossrh-staging-api.central.sonatype.com/service/local/`
- `https://central.sonatype.com/repository/maven-snapshots/`

## Core usage
```java
import com.thanhlv.dynamicjsonmapper.JsonTemplateMapper;
import tools.jackson.databind.JsonNode;

import java.util.Map;

JsonTemplateMapper mapper = JsonTemplateMapper.defaultInstance();
JsonNode output = mapper.render("templates/user-template.json", Map.of(
    "${name}", "Thanh",
    "${city}", "HCM"
));
String pretty = mapper.jsonNodeToString(output);
```

## Contributing
- Keep commits focused and descriptive (`type(scope): summary`).
- Update `docs/FEATURES.md` in the same change whenever behavior or templates are modified.

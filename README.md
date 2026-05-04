# DynamicJsonMapper

`DynamicJsonMapper` is a lightweight Java library for rendering JSON output from template files with runtime placeholder data.

## Why this project
- Keep API response shapes in JSON template files.
- Replace placeholder tokens (for example `${name}`, `${city}`) at runtime.
- Link parent and child templates for object/array rendering via `$template_ref` and `$source`.
- Support nested objects and arrays with recursive traversal.
- Reuse parsed templates with in-memory cache for better performance.

## Project structure
- `src/main/java/com/thanhlv/dynamicjsonmapper/`: library source (`JsonTemplateMapper`, `TemplateRenderer`, `TemplateReferenceResolver`, `TemplateRepository`, `CachingClasspathTemplateRepository`).
- `src/test/java/com/thanhlv/dynamicjsonmapper/`: JUnit test suites.
- `src/test/resources/templates/`: template fixtures used by tests and usage examples.
- `docs/FEATURES.md`: behavior-focused feature list.
- `AGENTS.md`: repository contribution and AI workflow guidelines.

## Tech stack
- Java 17 (Gradle toolchain)
- Jackson Databind `3.1.3`
- JUnit 5

## Build and test
```bash
./gradlew clean build
./gradlew test
./gradlew classes
```

## Core usage
```java
import com.thanhlv.dynamicjsonmapper.JsonTemplateMapper;
import tools.jackson.databind.JsonNode;

import java.util.Map;

JsonTemplateMapper mapper = JsonTemplateMapper.defaultInstance();
JsonNode output = mapper.render("templates/user-template.json", Map.of(
        "${name}", "Thanh",
        "${tuoi}", 23,
        "${noio}", "Ha Noi"
));
String pretty = mapper.jsonNodeToString(output);
```

## Custom template source
```java
import com.thanhlv.dynamicjsonmapper.JsonTemplateMapper;
import com.thanhlv.dynamicjsonmapper.TemplateRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

ObjectMapper objectMapper = new ObjectMapper();
TemplateRepository repository = new TemplateRepository() {
    @Override
    public JsonNode getTemplate(String templatePath) {
        try {
            return objectMapper.readTree("{\"message\": \"${message}\"}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
};

JsonTemplateMapper mapper = new JsonTemplateMapper(objectMapper, repository);
JsonNode result = mapper.render("ignored", Map.of("${message}", "hello"));
```

## Cache refresh
```java
mapper.refreshCache();
```

## Publish to Maven Central (GitHub Actions)
- Workflow file: `.github/workflows/publish-central.yml`.
- Trigger methods:
- Push tag release: `v*` (example: `v1.2.3`).
- Manual run via GitHub Actions (`workflow_dispatch`) with `release_version` input.
- Required GitHub repository secrets:
- `MAVEN_CENTRAL_USERNAME`
- `MAVEN_CENTRAL_PASSWORD`
- `MAVEN_CENTRAL_GPG_PRIVATE_KEY`
- `MAVEN_CENTRAL_GPG_PASSPHRASE`
- Build uses Gradle task: `publishToMavenCentral` with `-PreleaseVersion=...`.

## Publish to Maven Central (local script)
- Script file: `scripts/publish-central.sh`
- Usage:
```bash
export MAVEN_CENTRAL_USERNAME=...
export MAVEN_CENTRAL_PASSWORD=...
export MAVEN_GPG_PRIVATE_KEY=...
export MAVEN_GPG_PASSPHRASE=...
./scripts/publish-central.sh 1.2.3
```
- You can also provide the same values via Gradle property env vars:
- `ORG_GRADLE_PROJECT_mavenCentralUsername`
- `ORG_GRADLE_PROJECT_mavenCentralPassword`
- `ORG_GRADLE_PROJECT_signingInMemoryKey`
- `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`

## Run examples
Current repository examples are executable tests:
```bash
./gradlew test --tests com.thanhlv.dynamicjsonmapper.DynamicJsonMapperTest
./gradlew test --tests com.thanhlv.dynamicjsonmapper.DeepNestedMapperTest
./gradlew test --tests com.thanhlv.dynamicjsonmapper.PaginatedListMapperTest
```

## Contributing
- Keep commits focused and descriptive (`type(scope): summary`).
- Update `docs/FEATURES.md` in the same change whenever behavior or API usage changes.

## AI maintenance skills
- `skills/code-doc-sync`: reconcile docs and implementation (`docs-first` or `code-first`).
- `skills/code-test-sync`: keep implementation and JUnit tests synchronized when behavior changes.

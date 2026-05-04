# Current Features

This document describes the currently implemented features in `DynamicJsonMapper`.
Update this file whenever code changes behavior.
Current Java package namespace: `com.thanhlv.dynamicjsonmapper`.

## 1. JSON Template Rendering from Resource Files
- Implemented in `JsonTemplateMapper#render(...)`.
- Loads template JSON from classpath resources (example: `templates/user-template.json`).
- Replaces placeholder tokens in text nodes (pattern: `${key}`) with values from `rawData`.
- Works recursively for nested objects and arrays.

## 2. Linked Child Templates via `$template_ref` and `$source`
- Implemented in `TemplateReferenceResolver`.
- Supports linked child templates for response fields using object config:
- `$template_ref`: child template path in resources.
- `$source`: textual placeholder token (for example `${items}`) resolved from `rawData`.
- `$source` must resolve to an object or array value; otherwise rendering fails with `IllegalArgumentException` wrapped in `RuntimeException`.
- If `$source` resolves to `null`, renderer returns JSON `null` for that field.
- For primitive array items (for example `List<String>`), mapper auto-detects placeholders in the child template and binds each placeholder to the primitive item value.
- Primitive-array scenario is covered by templates `templates/tags-template.json` and `templates/tag-item-template.json`.

## 3. Template Caching for Faster Reuse
- Implemented in `CachingClasspathTemplateRepository` with `ConcurrentHashMap<String, JsonNode>`.
- Caches parsed templates by resource path to avoid repeated file I/O and parse cost.
- Uses `deepCopy()` before processing so cached templates are never mutated.

## 4. Cache Refresh Mechanism
- Implemented in `JsonTemplateMapper#refreshCache()`.
- Clears all cached template entries, useful when template files are changed at runtime.

## 5. Pretty JSON Output Utility
- Implemented in `JsonTemplateMapper#jsonNodeToString(...)`.
- Converts `JsonNode` to human-readable JSON for debugging or response inspection.

## 6. Deep-Nested Mapping via Shared Mapper
- Implemented in `DeepNestedMapper.main(...)` by calling `JsonTemplateMapper.defaultInstance().render(...)`.
- Uses `src/test/resources/templates/deep-nested-template.json` as nested template input.
- Reuses shared placeholder replacement logic and cache behavior from `JsonTemplateMapper`.

## 7. Runnable Demo Entrypoints
- `DynamicJsonMapper.main(...)`: demonstrates resource-template mapping flow (test/demo source set).
- `DeepNestedMapper.main(...)`: demonstrates deep-nested template mapping using `JsonTemplateMapper` default instance (test/demo source set).
- `PaginatedListMapper.main(...)`: demonstrates list slicing (`page`/`size`) and mapping paginated response fields (`total`, `total_pages`, `items`) via template placeholders (test/demo source set).

## 8. List Pagination Mapping Demo
- Implemented in `PaginatedListMapper.paginate(...)` and `PaginatedListMapper.main(...)`.
- Uses `src/test/resources/templates/paginated-users-template.json`.
- Uses `src/test/resources/templates/paginated-user-item-template.json` as item-level child template.
- Normalizes invalid pagination input so `page <= 0` becomes `1`.
- Normalizes invalid pagination input so `size <= 0` becomes `1`.
- Calculates `total` as the total number of source items.
- Calculates `total_pages` by `ceil(total / size)` with minimum value `1`.
- Keeps requested page number even when page exceeds available range, and returns empty `items` for out-of-range pages.
- Maps `items` by applying child template rendering to each current-page element.
- Exposes `first_item` and `last_item` in template output via the same child-template mechanism.

## 9. Configurable Repository Architecture
- `TemplateRepository` is an interface for custom template sources/strategies.
- `JsonTemplateMapper` is instance-based and accepts custom `TemplateRepository` via constructor.
- `JsonTemplateMapper.defaultInstance()` provides a static shared default instance for quick usage.

## 10. Packaged as Reusable Java Library
- Build uses Gradle `java-library` plugin.
- Publishes reusable Maven coordinates:
- Group: `com.thanhlv.dynamicjsonmapper`
- Artifact: `dynamic-json-mapper`
- Version: `1.0.0`
- Produces three artifacts: binary JAR, `-sources.jar`, and `-javadoc.jar`.
- `jackson-databind` is declared as `api` dependency because public signatures expose Jackson types (`JsonNode`, `ObjectMapper`).
- Supports local Maven publishing with `./gradlew publishToMavenLocal`.

## 11. Maven Central Release Automation
- Build includes `signing` and `io.github.gradle-nexus.publish-plugin` for Sonatype Central OSSRH Staging API flow.
- Publication now contains Maven Central-required POM metadata:
- project `name`/`description`/`url`
- `licenses`
- `developers`
- `scm`
- CI workflow `.github/workflows/publish-maven-central.yml` supports automated release:
- Uses `actions/checkout`, `actions/setup-java`, and `gradle/actions/setup-gradle`.
- Runs `./gradlew publish closeAndReleaseSonatypeStagingRepository` for publish + release staging.

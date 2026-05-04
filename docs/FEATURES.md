# Current Features

This document describes the implemented behavior in `DynamicJsonMapper`.
Current Java package namespace: `com.thanhlv.dynamicjsonmapper`.

## 1. JSON template rendering from classpath resources
- Implemented by `JsonTemplateMapper#render(...)` through `TemplateRenderer`.
- Loads template JSON by classpath path (for example `templates/user-template.json`).
- Recursively processes nested objects and arrays.
- Replaces textual placeholder tokens that exactly match `${key}` with values from `rawData`.
- If a placeholder key is missing in `rawData`, that field becomes JSON `null`.

## 2. Linked child templates via `$template_ref` and `$source`
- Implemented in `TemplateReferenceResolver`.
- Supports linked child templates using object config:
- `$template_ref`: child template path.
- `$source`: placeholder token (for example `${items}`) resolved from `rawData`.
- Validation rules:
- `$template_ref` and `$source` must both be present and textual.
- `$source` must be a placeholder token format (`${...}`).
- `$source` value must resolve to object or array; primitive source values fail with `IllegalArgumentException`.
- Rendering behavior:
- If `$source` resolves to `null`, the rendered field is JSON `null`.
- If `$source` resolves to an array, child template renders once per item.
- For object items, item properties are exposed to child template as placeholders (for example `id` -> `${id}`).
- For primitive items, all child-template placeholders are filled with that primitive value unless already provided by parent raw data.

## 3. Template repository abstraction and pluggable sources
- `TemplateRepository` is an interface for template-loading strategies.
- Default implementation is `CachingClasspathTemplateRepository`.
- `JsonTemplateMapper` supports constructor injection of custom `TemplateRepository` and/or custom `ObjectMapper`.

## 4. In-memory template caching
- `CachingClasspathTemplateRepository` caches parsed templates using `ConcurrentHashMap<String, JsonNode>`.
- Cached templates are reused across render calls.
- Rendering always uses `deepCopy()` of cached template to avoid mutation of cached entries.

## 5. Cache refresh
- `JsonTemplateMapper#refreshCache()` clears repository cache.
- For repositories that do not cache, `TemplateRepository#clear()` is optional (default no-op).

## 6. Shared default mapper and instance-based API
- `JsonTemplateMapper.defaultInstance()` provides a reusable singleton mapper.
- `JsonTemplateMapper` can also be instantiated directly for isolated configuration.
- Public API methods:
- `render(String templatePath, Map<String, Object> rawData)`
- `jsonNodeToString(JsonNode node)`
- `refreshCache()`

## 7. Pretty JSON output utility
- Implemented in `JsonTemplateMapper#jsonNodeToString(...)`.
- Produces pretty-printed JSON text from `JsonNode`.

## 8. Tested usage scenarios in repository
- `DynamicJsonMapperTest`: placeholder replacement for nested object fields.
- `DeepNestedMapperTest`: deep nested structure traversal and mapping.
- `PaginatedListMapperTest`: `$template_ref` array/object mapping, pagination helper behavior, and custom repository injection.

## 9. Maven Central publishing pipeline
- Build is configured with `com.vanniktech.maven.publish` in `build.gradle`.
- Publication metadata (coordinates, license, SCM, developer info) is defined in Gradle configuration.
- GitHub Actions workflow `.github/workflows/publish-central.yml` publishes on tag push (`v*`) or manual dispatch (`release_version` input).
- Local script `scripts/publish-central.sh` publishes with the same Gradle task and supports both `ORG_GRADLE_PROJECT_*` and `MAVEN_*` environment variable names.

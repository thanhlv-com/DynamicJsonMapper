# Current Features

This document describes the currently implemented features in `DynamicJsonMapper`.
Update this file whenever code changes behavior.
Current Java package namespace: `com.thanhlv.dynamicjsonmapper`.

## 1. JSON Template Rendering from Resource Files
- Implemented in `JsonTemplateMapper.render(...)`.
- Loads template JSON from classpath resources (example: `templates/user-template.json`).
- Replaces placeholder tokens in text nodes (pattern: `${key}`) with values from `rawData`.
- Works recursively for nested objects and arrays.

## 2. Linked Child Templates via `$template_ref` and `$source`
- Implemented in `JsonTemplateMapper.resolveTemplateRef(...)` and `renderChildTemplate(...)`.
- Supports linked child templates for response fields using object config:
- `$template_ref`: child template path in resources.
- `$source`: textual placeholder token (for example `${items}`) resolved from `rawData`.
- `$source` must resolve to an object or array value; otherwise rendering fails with `IllegalArgumentException` wrapped in `RuntimeException`.
- If `$source` resolves to `null`, renderer returns JSON `null` for that field.
- For primitive array items (for example `List<String>`), mapper auto-detects placeholders in the child template and binds each placeholder to the primitive item value.
- Primitive-array scenario is covered by templates `templates/tags-template.json` and `templates/tag-item-template.json`.

## 3. Template Caching for Faster Reuse
- Implemented in `JsonTemplateMapper` with `ConcurrentHashMap<String, JsonNode>`.
- Caches parsed templates by resource path to avoid repeated file I/O and parse cost.
- Uses `deepCopy()` before processing so cached templates are never mutated.

## 4. Cache Refresh Mechanism
- Implemented in `JsonTemplateMapper.refreshCache()`.
- Clears all cached template entries, useful when template files are changed at runtime.

## 5. Pretty JSON Output Utility
- Implemented in `JsonTemplateMapper.jsonNodeToString(...)`.
- Converts `JsonNode` to human-readable JSON for debugging or response inspection.

## 6. Deep-Nested Mapping via Shared Mapper
- Implemented in `DeepNestedMapper.main(...)` by calling `JsonTemplateMapper.render(...)`.
- Uses `src/test/resources/templates/deep-nested-template.json` as nested template input.
- Reuses shared placeholder replacement logic and cache behavior from `JsonTemplateMapper`.

## 7. Runnable Demo Entrypoints
- `DynamicJsonMapper.main(...)`: demonstrates resource-template mapping flow (test/demo source set).
- `DeepNestedMapper.main(...)`: demonstrates deep-nested template mapping using `JsonTemplateMapper` (test/demo source set).
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

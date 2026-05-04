# Current Features

This document describes the currently implemented features in `DynamicJsonMapper`.  
Update this file whenever code changes behavior.

## 1. JSON Template Rendering from Resource Files
- Implemented in `JsonTemplateMapper.render(...)`.
- Loads template JSON from `src/main/resources` (example: `templates/user-template.json`).
- Replaces placeholder tokens in text nodes (pattern: `${key}`) with values from `rawData`.
- Works recursively for nested objects and arrays.

## 2. Template Caching for Faster Reuse
- Implemented in `JsonTemplateMapper` with `ConcurrentHashMap<String, JsonNode>`.
- Caches parsed templates by resource path to avoid repeated file I/O and parse cost.
- Uses `deepCopy()` before processing so cached templates are never mutated.

## 3. Cache Refresh Mechanism
- Implemented in `JsonTemplateMapper.refreshCache()`.
- Clears all cached template entries, useful when template files are changed at runtime.

## 4. Pretty JSON Output Utility
- Implemented in `JsonTemplateMapper.jsonNodeToString(...)`.
- Converts `JsonNode` to human-readable JSON for debugging or response inspection.

## 5. Deep-Nested Mapping via Shared Mapper
- Implemented in `DeepNestedMapper.main(...)` by calling `JsonTemplateMapper.render(...)`.
- Uses `src/main/resources/templates/deep-nested-template.json` as nested template input.
- Reuses shared placeholder replacement logic and cache behavior from `JsonTemplateMapper`.

## 6. Runnable Demo Entrypoints
- `DynamicJsonMapper.main(...)`: demonstrates resource-template mapping flow.
- `DeepNestedMapper.main(...)`: demonstrates deep-nested template mapping using `JsonTemplateMapper`.
- `PaginatedListMapper.main(...)`: demonstrates list slicing (`page`/`size`) and mapping paginated response fields (`total`, `total_pages`, `items`) via template placeholders.

## 7. List Pagination Mapping Demo
- Implemented in `PaginatedListMapper.paginate(...)` and `PaginatedListMapper.main(...)`.
- Uses `src/main/resources/templates/paginated-users-template.json`.
- Normalizes invalid pagination input so `page <= 0` becomes `1`.
- Normalizes invalid pagination input so `size <= 0` becomes `1`.
- Calculates `total` as the total number of source items.
- Calculates `total_pages` by `ceil(total / size)` with minimum value `1`.
- Maps `items` as the current page sub-list (empty if page exceeds available data).

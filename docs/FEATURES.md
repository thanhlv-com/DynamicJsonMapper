# Current Features

This document describes the currently implemented features in `DynamicJsonMapper`.  
Update this file whenever code changes behavior.

## 1. JSON Template Rendering from Resource Files
- Implemented in `JsonTemplateMapper.render(...)`.
- Loads template JSON from `src/main/resources` (example: `templates/user-template.json`).
- Replaces placeholder tokens in text nodes (pattern: `[key]`) with values from `rawData`.
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

## 5. Recursive Deep-Nested Mapping (Map-Based Template)
- Implemented in `DeepNestedMapper.mapRecursive(...)`.
- Accepts a nested `Map<String, Object>` template and maps source placeholders to a deeply nested output `ObjectNode`.
- If a source key is missing, writes `null`.

## 6. Runnable Demo Entrypoints
- `DynamicJsonMapper.main(...)`: demonstrates resource-template mapping flow.
- `DeepNestedMapper.main(...)`: demonstrates recursive nested mapping flow.

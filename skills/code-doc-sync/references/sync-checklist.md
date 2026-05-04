# Sync Checklist

## Drift Detection
- Package and class names match docs examples.
- Template paths in code match README/docs examples.
- Build/test commands in docs are runnable in current Gradle setup.
- Feature list in `docs/FEATURES.md` matches current implementation.

## docs-first Actions
- Patch code to satisfy documented behavior.
- Keep public method signatures consistent with docs where possible.
- Update docs only for ambiguity or missing edge-case notes.

## code-first Actions
- Patch docs to reflect real implementation and current limitations.
- Update command snippets and expected outputs.
- Remove stale claims not backed by code/tests.

## Final Gate
- No contradiction remains between:
  - `README.md`
  - `docs/FEATURES.md`
  - `AGENTS.md`
  - Current Java sources under `src/main/java/`

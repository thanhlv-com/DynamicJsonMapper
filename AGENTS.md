# Repository Guidelines

## Project Structure & Module Organization
This is a single-module Gradle Java project (`settings.gradle` name: `DynamicJsonMapper`).

- `src/main/java/com/thanhlv/DynamicJsonMapper/`: core mapping logic and runnable examples (`DynamicJsonMapper`, `JsonTemplateMapper`, `DeepNestedMapper`).
- `src/main/resources/templates/`: JSON templates used by mappers (for example `user-template.json`).
- `src/test/java/` and `src/test/resources/`: test code and fixtures (currently scaffolded, add tests here).
- `docs/`: living documentation for implemented features (`docs/FEATURES.md` must reflect current behavior).
- `build/`: generated outputs and reports (do not edit manually).

Keep new Java classes under `com.thanhlv.DynamicJsonMapper` unless package boundaries are intentionally introduced.

## Build, Test, and Development Commands
Use the Gradle wrapper from repository root:

- `./gradlew clean build`: compiles code, runs tests, and produces artifacts.
- `./gradlew test`: runs JUnit 5 tests only.
- `./gradlew classes`: compiles main sources without running tests.
- `./gradlew clean`: removes generated build outputs.

Run demo entry points from your IDE or after build using the compiled classpath.

## Coding Style & Naming Conventions
- Follow standard Java style: 4-space indentation, UTF-8 files, and one public class per file.
- Use `PascalCase` for classes (`JsonTemplateMapper`), `camelCase` for methods/variables, and `UPPER_SNAKE_CASE` for constants.
- Prefer descriptive method names tied to mapping intent (for example `renderTemplate`, `extractNestedValue`).
- Keep resource file names lowercase with hyphens when needed (for example `order-template.json`).

## Testing Guidelines
- Test framework: JUnit 5 (`useJUnitPlatform()` configured in `build.gradle`).
- Place tests in `src/test/java` mirroring package structure from `src/main/java`.
- Name test classes with `*Test` suffix (for example `JsonTemplateMapperTest`).
- Cover placeholder replacement, nested object traversal, and invalid/missing key behavior.

## Commit & Pull Request Guidelines
Git history is not available in this workspace snapshot, so follow this convention:

- Commit format: `type(scope): short summary` (for example `feat(mapper): support nested arrays`).
- Keep commits focused and buildable.
- PRs should include: change summary, test evidence (`./gradlew test` output), related issue/task, and sample input/output JSON when behavior changes.

## Documentation Update Rule (AI and Contributors)
- Any code change that affects behavior, APIs, templates, or examples must update `docs/FEATURES.md` in the same PR/commit.
- AI agents working in this repository must treat `docs/FEATURES.md` as a required update target, not an optional follow-up.
- If no documentation change is needed, explicitly state why in the PR description.

## AI Skill: Code/Docs Synchronization
- Use skill path `skills/code-doc-sync` when reconciling mismatches between implementation and docs.
- Supported priority modes:
- `docs-first`: docs are source of truth; update code to match docs.
- `code-first`: code is source of truth; update docs to match implementation.
- If mode is not specified, AI should ask one short question before editing.

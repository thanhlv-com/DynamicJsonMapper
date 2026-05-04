---
name: code-test-sync
description: Synchronize implementation and unit tests after repository changes. Use when code updates may introduce behavior drift and tests are missing or outdated.
---

# Code Test Sync

Keep Java implementation and JUnit tests synchronized so behavior changes are covered by automated checks.

## Trigger
Use this skill when:
- Code under `src/main/java/**` changed.
- A bug fix or feature was added but no matching test exists.
- Existing tests assert outdated behavior.

## Workflow
1. Inventory changed logic:
- Identify modified public methods, edge-case handling, and return shapes.
2. Map to test coverage:
- Find corresponding test classes under `src/test/java/**`.
- Mark uncovered scenarios as required additions.
3. Patch tests first where possible:
- Add or update `*Test` classes to mirror package/class structure.
- Cover happy path, boundary cases, and invalid input handling.
4. Patch implementation only if test failures reveal defects:
- Keep fixes minimal and tied to failing assertions.
5. Validate:
- Run `./gradlew test`.
- Ensure new tests fail before fix and pass after fix when feasible.
6. Report:
- List changed code paths and the exact tests that now cover them.

## Repository Rules
- If implementation behavior changes, unit tests must be updated in the same change.
- If no test update is needed, explicitly state why the existing coverage already applies.
- Prefer deterministic tests without external I/O or time dependence.

## Missing Coverage Policy
- If a changed behavior has no unit test, add one before finalizing.
- If the codebase has no existing test class for that component, create a new `*Test` file in mirrored package path.

## Reference
Use checklist: `references/sync-checklist.md`.

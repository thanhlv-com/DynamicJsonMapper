# Code/Test Sync Checklist

## Change Scan
- Identify all changed files under `src/main/java/**`.
- List modified methods and their behavior impact.
- Confirm whether API/output shape changed.

## Coverage Scan
- Check for existing `*Test` class for each changed component.
- Verify assertions include:
- Happy path.
- Invalid input handling.
- Boundary/index/range behavior.
- Empty/null data behavior when applicable.

## Required Actions
- Add missing tests for uncovered changed behavior.
- Update outdated assertions to match intended behavior.
- Remove or rewrite flaky test logic.

## Final Gate
- `./gradlew test` passes.
- Every behavior change has at least one direct unit-test assertion.
- No unresolved mismatch remains between implementation and tests.

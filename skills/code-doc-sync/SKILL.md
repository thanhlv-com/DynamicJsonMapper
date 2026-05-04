---
name: code-doc-sync
description: Synchronize implementation and documentation after repository changes. Use when code updates may invalidate README, docs/FEATURES.md, AGENTS.md, API examples, or usage instructions, and when the user wants to choose source-of-truth priority (docs-first or code-first) before reconciling differences.
---

# Code Doc Sync

Reconcile code and docs with an explicit priority mode so the result is consistent and reviewable.

## Priority Modes
Choose one mode before editing:

- `docs-first`: Treat documentation as source of truth. Update code/examples/tests to match docs unless docs are obviously wrong or outdated.
- `code-first`: Treat implementation as source of truth. Update docs/examples/usage instructions to match current behavior.

If mode is not provided, ask one concise question: `Ưu tiên docs-first hay code-first?`

## Workflow
1. Inventory changed surfaces:
- Code: `src/main/java/**`, configs, templates, API/output behavior.
- Docs: `README.md`, `docs/**`, `AGENTS.md`, inline examples.
2. Find mismatches:
- Names/packages, commands, behavior claims, sample outputs, and feature lists.
3. Apply mode decision:
- `docs-first`: patch code first, then adjust docs minimally for clarity.
- `code-first`: patch docs first, then adjust tests/examples to prevent drift.
4. Validate:
- Build or run targeted checks.
- Re-read changed docs to ensure they match final code.
5. Report:
- Summarize what was treated as truth and what files were reconciled.

## Repository Rules
- If behavior changes, always update `docs/FEATURES.md` in the same change.
- If package/class names change, update README run instructions and any path references.
- Never leave unresolved contradiction between docs and code in one commit.

## Reference
Use checklist: `references/sync-checklist.md`.

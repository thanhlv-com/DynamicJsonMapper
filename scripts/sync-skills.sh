#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_SKILLS_DIR="${PROJECT_ROOT}/skills"
CODEX_SKILLS_DIR="${HOME}/.codex/skills"

if [[ ! -d "${PROJECT_SKILLS_DIR}" ]]; then
  echo "No project skills directory found: ${PROJECT_SKILLS_DIR}"
  exit 0
fi

mkdir -p "${CODEX_SKILLS_DIR}"

linked_count=0

for skill_dir in "${PROJECT_SKILLS_DIR}"/*; do
  [[ -d "${skill_dir}" ]] || continue
  [[ -f "${skill_dir}/SKILL.md" ]] || continue

  skill_name="$(basename "${skill_dir}")"
  target_link="${CODEX_SKILLS_DIR}/${skill_name}"

  ln -sfn "${skill_dir}" "${target_link}"
  echo "Linked ${skill_name} -> ${target_link}"
  linked_count=$((linked_count + 1))
done

echo "Done. Linked ${linked_count} skill(s) from ${PROJECT_SKILLS_DIR} to ${CODEX_SKILLS_DIR}."

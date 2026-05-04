#!/usr/bin/env bash

set -euo pipefail

if [[ "${1:-}" == "" ]]; then
  echo "Usage: $0 <release-version>"
  echo "Example: $0 1.2.3"
  exit 1
fi

RELEASE_VERSION="$1"

export ORG_GRADLE_PROJECT_mavenCentralUsername="${ORG_GRADLE_PROJECT_mavenCentralUsername:-${MAVEN_CENTRAL_USERNAME:-}}"
export ORG_GRADLE_PROJECT_mavenCentralPassword="${ORG_GRADLE_PROJECT_mavenCentralPassword:-${MAVEN_CENTRAL_PASSWORD:-}}"
export ORG_GRADLE_PROJECT_signingInMemoryKey="${ORG_GRADLE_PROJECT_signingInMemoryKey:-${MAVEN_GPG_PRIVATE_KEY:-}}"
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword="${ORG_GRADLE_PROJECT_signingInMemoryKeyPassword:-${MAVEN_GPG_PASSPHRASE:-}}"

required_vars=(
  ORG_GRADLE_PROJECT_mavenCentralUsername
  ORG_GRADLE_PROJECT_mavenCentralPassword
  ORG_GRADLE_PROJECT_signingInMemoryKey
  ORG_GRADLE_PROJECT_signingInMemoryKeyPassword
)

for var_name in "${required_vars[@]}"; do
  if [[ -z "${!var_name}" ]]; then
    echo "Missing required environment variable: ${var_name}" >&2
    echo "You can also provide aliases: MAVEN_CENTRAL_USERNAME, MAVEN_CENTRAL_PASSWORD, MAVEN_GPG_PRIVATE_KEY, MAVEN_GPG_PASSPHRASE" >&2
    exit 1
  fi
done

echo "Publishing version ${RELEASE_VERSION} to Maven Central..."
GRADLE_USER_HOME="${GRADLE_USER_HOME:-.gradle-cache}" ./gradlew clean publishToMavenCentral -PreleaseVersion="${RELEASE_VERSION}" --no-daemon --stacktrace

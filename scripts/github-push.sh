#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Air Arsenal — GitHub push script
# Stages every change, commits with an auto-generated message, and pushes to
# the GitHub remote.  Run via the "Push to GitHub" workflow in Replit.
# ─────────────────────────────────────────────────────────────────────────────

set -euo pipefail

# ── Safety checks ─────────────────────────────────────────────────────────────
if [ -z "${GITHUB_PERSONAL_ACCESS_TOKEN:-}" ]; then
  echo "❌  GITHUB_PERSONAL_ACCESS_TOKEN is not set."
  echo "    Add it as a Replit Secret and retry."
  exit 1
fi

GITHUB_USER="TITANICBHAI"
REPO_NAME="air-arsenal-mod"
REMOTE_URL="https://${GITHUB_USER}:${GITHUB_PERSONAL_ACCESS_TOKEN}@github.com/${GITHUB_USER}/${REPO_NAME}.git"
BRANCH="main"

# ── Ensure the 'github' remote is configured with the live token ──────────────
if git remote get-url github &>/dev/null; then
  git remote set-url github "$REMOTE_URL"
else
  git remote add github "$REMOTE_URL"
fi

# ── Stage all changes ─────────────────────────────────────────────────────────
git add -A

# ── Commit only if there is something new ─────────────────────────────────────
if git diff --cached --quiet; then
  echo "ℹ️   Nothing to commit — working tree is clean."
else
  TIMESTAMP=$(date -u '+%Y-%m-%d %H:%M UTC')
  git commit -m "chore: auto-push ${TIMESTAMP}"
  echo "✅  Committed at ${TIMESTAMP}"
fi

# ── Push ──────────────────────────────────────────────────────────────────────
echo "🚀  Pushing to github/${BRANCH} …"
git push github "$BRANCH"
echo "✅  Push complete → https://github.com/${GITHUB_USER}/${REPO_NAME}"

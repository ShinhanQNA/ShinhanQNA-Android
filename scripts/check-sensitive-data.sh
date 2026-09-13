#!/usr/bin/env sh
set -eu

added_lines=$(cat)
[ -z "$added_lines" ] && exit 0

if printf '%s\n' "$added_lines" | grep -Eq '([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}|01[016789][[:space:]-]?[0-9]{3,4}[[:space:]-]?[0-9]{4}|[0-9]{6}-?[1-4][0-9]{6}|AKIA[0-9A-Z]{16}|AIza[0-9A-Za-z_-]{35}|gh[pousr]_[A-Za-z0-9_]{20,}|sk-[A-Za-z0-9_-]{20,}|xox[baprs]-[A-Za-z0-9-]{10,}|eyJ[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}\.[A-Za-z0-9_-]{10,}|[Bb]earer[[:space:]]+[A-Za-z0-9._~-]{20,}|(password|passwd|secret|api[_-]?key)[[:space:]]*[:=][[:space:]]*"[^"]{12,}")'; then
  echo "민감정보 또는 자격증명으로 보이는 값이 추가되었습니다. 값을 제거하거나 안전한 설정으로 분리하세요." >&2
  exit 1
fi

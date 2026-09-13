# Shinhan QnA 팀 개발 규칙

이 문서는 저장소에서 작업하는 사람과 AI 에이전트가 공통으로 따르는 규칙의 진입점입니다. Android 규칙은 `.codex/rules/android.md`에, 반복 작업 절차는 `.agents/skills/`에 둡니다.

## 작업 원칙

- 모든 기능과 구조 변경은 구현 전에 구현 방향, 변경 범위, 제외 범위를 공유하고 승인을 받은 뒤 시작합니다.
- API 명세와 관련 문서, 기존 코드를 먼저 확인하고 기존 구조와 네이밍을 따릅니다.
- 요청받지 않은 기능, 리팩토링, 추상화를 추가하지 않습니다.
- 변경 범위는 최소화하고 필요한 테스트와 문서만 함께 수정합니다.
- 민감정보, API 키, 개인정보를 코드, 로그, 응답, 커밋에 남기지 않습니다.
- 실패한 테스트를 삭제하거나 비활성화해 통과시키지 않습니다.
- `main`에 직접 커밋하거나 강제 푸시하지 않습니다.

## 승인 절차

- 이슈·커밋·푸시·PR·리뷰처럼 Git 또는 GitHub에 쓰는 작업은 실행 직전에 대상과 최종 내용을 보여주고 명시적인 승인을 받습니다.
- 구현 승인은 커밋, 푸시, 이슈, PR, 리뷰 승인으로 간주하지 않습니다. 각 쓰기 작업마다 다시 승인받습니다.
- 읽기, 조사, 로컬 초안 작성은 승인 없이 할 수 있지만 승인 전에는 실제 쓰기 명령을 실행하지 않습니다.

## 규칙 문서

- Android: `.codex/rules/android.md`
- 커밋: `.codex/commit-convention.md`

## 작업 절차

- 모든 에이전트는 작업 전에 이 문서와 `.codex/rules/android.md`를 읽습니다.
- Android 기능 구현은 `android-feature` 절차를 따릅니다.
- 개인 승인 절차가 필요한 경우 로컬 `.agents/skills/personal-harness/` 규칙을 함께 따릅니다.

## 검증 명령

- 변경 검증: `./gradlew :app:testDebugUnitTest`
- PR 전 검증: `./gradlew clean :app:assembleDebug`

로컬 Hook은 `.githooks/`에 있습니다. 최초 1회 `./scripts/install-hooks.sh`로 활성화합니다.

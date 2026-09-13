# Shinhan QnA 팀 하네스

| 구성 | 저장 위치 | 역할 |
| --- | --- | --- |
| Rule | `AGENTS.md`, `.codex/rules/` | 항상 지켜야 하는 개발 원칙 |
| Hook | `.githooks/` | 로컬에서 자동으로 차단·검증할 항목 |
| Skill | `.agents/skills/` | 반복 작업 절차 |
| Template | `.github/`, `.codex/` | 이슈·PR·커밋 형식 통일 |

## 강제 수준

- 구현 방향 승인은 Rule과 Skill, Issue·PR 템플릿으로 관리합니다. Hook으로 승인 자체를 판별하지 않습니다.
- 커밋 메시지, 보호 브랜치, 민감정보, 충돌 마커, 대용량 파일은 Hook으로 빠르게 차단합니다.
- 민감정보 검사는 로컬 Hook과 PR workflow에서 같은 규칙으로 검증합니다.
- 단위 테스트와 빌드는 로컬 Hook과 PR CI에서 검증합니다.
- `main` 직접 푸시와 PR 병합 승인은 GitHub Branch protection에서 별도로 설정합니다. 저장소 파일만으로는 원격 권한을 보장할 수 없습니다.

## 시작 방법

```bash
./scripts/install-hooks.sh
```

기능 작업은 `android-feature` 절차를 따릅니다.

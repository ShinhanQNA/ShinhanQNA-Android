---
name: android-feature
description: Shinhan QnA Android 기능을 구현할 때 사용하는 절차
---

# Android 기능 구현

1. 관련 API 명세, 기존 화면, `ViewModel`, Repository, 네비게이션 구조를 확인합니다.
2. 구현 범위, 제외 범위, 영향받는 화면·API·테스트를 정리해 공유합니다.
3. 구현 방향을 제안하고 승인받습니다. 승인 전에는 코드 수정을 시작하지 않습니다.
4. Composable, `ViewModel`, Repository의 책임을 나눠 최소한으로 구현합니다.
5. 화면 상태는 `UiState`로 관리하고, 오류·로딩·빈 화면·입력 검증을 필요한 범위에서 처리합니다.
6. 비즈니스 분기나 데이터 변환에는 작은 단위 테스트를 작성합니다.
7. `./gradlew :app:testDebugUnitTest`를 실행하고, PR 전 `./gradlew clean :app:assembleDebug`를 실행합니다.

승인받지 않은 기능·리팩토링, Composable의 네트워크 호출, 전역 Scope 사용, 민감정보 로그, 검증 우회는 금지합니다.

# Logging Policy

## 금지 데이터

모든 빌드에서 access token, refresh token, Authorization 헤더, 비밀번호, OAuth code,
HTTP 요청·응답 body, 이메일·학번·이름, 게시글·이의신청 본문, 이미지 경로를 로그에 남기지 않는다.

## 허용 데이터

- release: 기능 이름, 고정 오류 문구, HTTP 상태 코드, 서버 오류 코드, request ID, 처리 시간
- debug: release 허용 데이터와 마스킹된 식별자, 데이터 건수

`Log.e`에는 원문 예외나 `errorBody()`를 전달하지 않는다. 서버 오류는 안전한 코드와
고정 문구로 변환해 기록한다.

## 구현 규칙

- `debugLog()`만 debug 진단 로그에 사용한다. release 빌드에서는 출력되지 않는다.
- HTTP body 로그는 모든 빌드에서 사용하지 않는다.
- 상세 payload 확인은 스테이징의 테스트 계정에서 브레이크포인트 또는 로컬 프록시로 일회성 수행한다.

## 검토 항목

- 새 로그에 금지 데이터나 `Throwable` 원문이 없는지 확인한다.
- 새 네트워크 오류 처리에는 HTTP 상태 코드와 서버 오류 코드만 추가한다.

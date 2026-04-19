# TASKS: 관리자 세션 로그인

- [x] T1. SecurityConfig 수정 — 경로별 인증 & Form Login 설정 (완료일: 2026-04-19)
  - 목적: `/admin/**` 인증 필수, `/api/**` CSRF 제외, 로그인/로그아웃 경로 설정
  - 작업 내용:
    - `csrf { ignoringRequestMatchers("/api/**") }` 로 API 경로 CSRF 제외
    - `authorizeHttpRequests`: `/admin/login` permitAll, `/admin/**` authenticated, 나머지 permitAll
    - `formLogin` 블록: loginPage, loginProcessingUrl, defaultSuccessUrl, failureUrl
    - `logout` 블록: logoutUrl, logoutSuccessUrl, invalidateHttpSession, deleteCookies
    - `InMemoryUserDetailsManager` 빈 추가 (환경변수 주입)
  - 예상 변경 파일: `SecurityConfig.kt`
  - 완료 기준: 앱 구동 시 `/admin/dashboard` 접근 → `/admin/login` 리다이렉트

- [x] T2. 세션 타임아웃 & 쿠키 설정 (완료일: 2026-04-19)
  - 목적: 세션 유효시간 3시간, 프로덕션 Secure 쿠키
  - 작업 내용:
    - `application.yml`에 `server.servlet.session.timeout: 3h`, `cookie.http-only: true`
    - `application-prod.yml`에 `cookie.secure: true`
    - `application.yml`에 `admin.username/password` 환경변수 바인딩 추가
    - `application.yml`의 `spring.security.user` dummy 설정 제거
  - 예상 변경 파일: `application.yml`, `application-prod.yml`
  - 완료 기준: 로컬 기본값으로 앱 구동 성공

- [x] T3. AdminLoginController & 로그인 페이지 구현 (완료일: 2026-04-19)
  - 목적: GET `/admin/login` 요청에 로그인 폼 렌더링
  - 작업 내용:
    - `AdminLoginController.kt` 신규 — GET `/admin/login` → `admin/login` 뷰 반환
    - `login.html` 신규 — 기존 관리자 UI 스타일, ID/PW 입력 폼, CSRF 토큰, 오류 메시지(`?error`)
  - 예상 변경 파일: `AdminLoginController.kt` (신규), `login.html` (신규)
  - 완료 기준: 로그인 페이지 렌더링, 폼 제출 시 Spring Security가 처리

- [x] T4. 통합 테스트 작성 (완료일: 2026-04-19)
  - 목적: 인증 흐름 전체 검증
  - 작업 내용:
    - 미인증 `/admin/dashboard` → 302 리다이렉트 확인
    - 잘못된 자격증명 → `/admin/login?error=true` 확인
    - 정상 로그인 → 대시보드 접근 성공 확인
    - 로그아웃 후 세션 무효화 확인
    - `/api/places` POST CSRF 토큰 없이 200 확인 (API 경로 CSRF 제외)
  - 예상 변경 파일: `AdminLoginIntegrationTest.kt` (신규)
  - 완료 기준: 전체 테스트 통과

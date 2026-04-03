# Tarae 프로젝트 백로그

## 🔥 High Priority

| ID | 기능 | 설명 | 상태 |
|----|------|------|------|
| B-01 | Phase 0: 프로젝트 셋업 | docker-compose(MySQL), build.gradle 의존성, application.yml, BaseEntity, SecurityConfig(임시 permitAll), GlobalExceptionHandler | 대기 |
| B-02 | Place 도메인 | categories 테이블, places + 관련 테이블 DDL, Entity/Repository/Service/Controller, DataLoader(초기 데이터) | 대기 |
| B-03 | Event 도메인 | events 테이블 DDL, Entity/Repository/Service/Controller | 대기 |
| B-04 | 제보 스키마 설계 | place_submissions, event_submissions 스키마 확정 및 DDL — B-02/B-03 완료 후 진행 | 대기 |
| B-05 | 제보 API 구현 | POST /api/v1/submissions/places, POST /api/v1/submissions/events, 관리자 승인 흐름 | 대기 |

## 📋 Medium Priority

| ID | 기능 | 설명 | 상태 |
|----|------|------|------|
| B-06 | 관리자 API | 제보 목록 조회/승인/거절 | 대기 |
| B-07 | 인증/인가 | Spring Security JWT, UserRole(guest/user/admin) | 대기 |

## ⚙️ 코드 작성 전 정해야 할 것

> Phase 0 셋업 시작 전에 논의하고 확정할 항목들.

| 항목 | 내용 | 상태 |
|------|------|------|
| 커밋 메시지 컨벤션 | `type: 한글 설명` — Conventional Commits 기반, .claude/rules/commit-conventions.md 정의됨 | 완료 |
| ktlint/ktfmt 포맷터 | 코드 포맷팅 자동화. PostToolUse 훅 연동 가능 | 미정 |
| 위험 명령 차단 훅 | `git push --force`, `git reset --hard` 등 PreToolUse 훅 | 미정 |
| AbstractIntegrationTest | Testcontainers 안 쓰더라도 H2 공통 설정 베이스 클래스 필요한지 | 미정 |

## 🧊 Icebox (나중에)

| ID | 기능 | 설명 | 상태 |
|----|------|------|------|
| B-08 | 테스터 모집 | tester_recruitments, tester_applications 테이블 및 API | 보류 |

## 📌 프론트엔드 협의 필요

→ 상세 내용은 [`docs/collab-notes.md`](../collab-notes.md) 참고

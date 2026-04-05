# Tarae 프로젝트 백로그

## 🔥 High Priority

| ID | 기능 | 설명 | 등록일 | 상태 |
|----|------|------|--------|------|
| B-01 | Phase 0: 프로젝트 셋업 | docker-compose(MySQL), build.gradle 의존성, application.yml, BaseEntity, SecurityConfig(임시 permitAll), GlobalExceptionHandler | 2026-04-04 | ✅ 완료 |
| B-02 | Place 도메인 | categories 테이블, places + 관련 테이블 DDL, Entity/Repository/Service/Controller | 2026-04-04 | ✅ 완료 |
| B-03 | Event 도메인 | events 테이블 DDL, Entity/Repository/Service/Controller | 2026-04-04 | ✅ 완료 |
| B-04 | 제보 스키마 설계 | place_submissions, event_submissions 스키마 확정 및 DDL | 2026-04-04 | ✅ 완료 |
| B-05 | 제보 API 구현 | POST /api/submissions/places, POST /api/submissions/events | 2026-04-04 | ✅ 완료 |

## 📋 Medium Priority

| ID | 기능 | 설명 | 등록일 | 상태 |
|----|------|------|--------|------|
| B-06 | 관리자 페이지 (Thymeleaf) | 대시보드, 제보 목록/검수 (장소·이벤트), 이벤트 만료 스케줄러 | 2026-04-04 | ✅ 완료 |
| B-07 | 인증/인가 | Spring Security JWT, UserRole(guest/user/admin) | 2026-04-04 | 대기 |

## ⚙️ 코드 작성 전 정해야 할 것

> Phase 0 셋업 시작 전에 논의하고 확정할 항목들.

| 항목 | 내용 | 등록일 | 상태 |
|------|------|--------|------|
| 커밋 메시지 컨벤션 | `type: 한글 설명` — Conventional Commits 기반, .claude/rules/commit-conventions.md 정의됨 | 2026-04-04 | 완료 |
| ktlint/ktfmt 포맷터 | 코드 포맷팅 자동화. PostToolUse 훅 연동 가능 | 2026-04-04 | 미정 |
| 위험 명령 차단 훅 | `git push --force`, `git reset --hard` 등 PreToolUse 훅 | 2026-04-04 | 미정 |
| AbstractIntegrationTest | H2 공통 설정 베이스 클래스 필요한지 | 2026-04-04 | 미정 |

## 🧊 Icebox (나중에)

| ID | 기능 | 설명 | 등록일 | 상태 |
|----|------|------|--------|------|
| B-08 | 테스터 모집 | tester_recruitments, tester_applications 테이블 및 API | 2026-04-04 | 보류 |

## 📌 프론트엔드 협의 필요

→ 상세 내용은 [`docs/collab-notes.md`](../collab-notes.md) 참고

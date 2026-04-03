# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build

```bash
./gradlew build
./gradlew bootRun
./gradlew test
./gradlew test --tests "com.taraethreads.tarae.SomeTest"
```

Requires JDK 17.

## 개발 원칙

- 사용자 요청을 맹목적으로 따르지 않는다. 기술적으로 더 나은 방향이 있으면 근거를 들어 먼저 제안한다.
- 모든 기능 개발은 TDD (Red→Green→Refactor). 테스트 없이 프로덕션 코드 작성 금지.
- 기능 개발 전 반드시 Spec-Driven 순서: PRD→SDD→TASKS→승인→구현. Task 범위 밖 변경 금지.
- 코프링 처음 사용하는 기능은 `docs/tech-notes/learning/`에 개념부터 설명하며 기록.
- 비자명한 기술 결정, 문제 해결, 인사이트는 `docs/tech-notes/`에 자동 기록.
- Entity/API/도메인 구조 변경 시 `docs/architecture/` 해당 파일 업데이트.
- 기획/마케팅 논의는 `docs/ideas/`, 횡단 기술 결정은 `docs/decisions/ADR-NNN.md`.

## 아키텍처

- 패키지: `com.taraethreads.tarae.{domain}.{layer}`
- 레이어: Controller → Service → Repository
- DTO ↔ Entity 완전 분리, Entity 직접 응답 노출 금지
- DTO 네이밍: `~Request` / `~Response`

## 규칙 & 참고 문서

@.claude/rules/kotlin-conventions.md
@.claude/rules/test-conventions.md
@.claude/rules/jpa-entity-rules.md
@.claude/rules/swagger-conventions.md
@docs/architecture/overview.md

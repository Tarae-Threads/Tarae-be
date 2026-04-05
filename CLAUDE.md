# CLAUDE.md

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

## 에이전트/스킬 사용 규칙

아래 상황에서는 반드시 해당 에이전트 또는 스킬을 사용할 것. 판단을 미루거나 직접 처리하지 않는다.

| 상황 | 사용 | 비고 |
|------|------|------|
| 설계 논의 (DB, API, 도메인, 아키텍처) | `architect` 에이전트 | opus, 깊은 추론 |
| 코드 리뷰 | `code-reviewer` 에이전트 | sonnet, 패턴 매칭 |
| 기획/마케팅/서비스 방향 깊은 논의 | `product-advisor` 에이전트 | opus, 전략적 사고 |
| 새 기능 개발 시작 | `/spec` 스킬 | PRD→SDD→TASKS |
| 백로그 추가/조회/정리 | `/backlog` 스킬 | |
| 기술 인사이트 기록 | `/tech-note` 스킬 | |
| 간단한 아이디어 메모 | `/idea` 스킬 | |

## 규칙 참고

코드 작성 시 `.claude/rules/` 하위 컨벤션이 경로 기반으로 자동 적용됨:
- `kotlin-conventions.md` → `src/main/**/*.kt`
- `test-conventions.md` → `src/test/**/*.kt`
- `jpa-entity-rules.md` → `src/main/**/domain/**/*.kt`
- `swagger-conventions.md` → `src/main/**/controller/**/*.kt`, `src/main/**/dto/**/*.kt`
- `docs-maintenance.md` → 항상 적용 (Task 완료, 문서 동기화)

@docs/architecture/overview.md

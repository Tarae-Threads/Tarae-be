# TASKS: B-03 Event 도메인
- 작성일: 2026-04-05

- [x] T1. Flyway V2 마이그레이션 작성
  - 목적: events 테이블 생성 DDL
  - 예상 변경 파일:
    - `src/main/resources/db/migration/V2__create_event_tables.sql` (신규)
  - 완료일: 2026-04-05

- [x] T2. Event 도메인 Entity 구현
  - 목적: JPA Entity 정의
  - 예상 변경 파일:
    - `src/main/kotlin/.../event/domain/Event.kt` (신규)
    - `src/main/kotlin/.../event/domain/EventType.kt` (신규)
  - 완료일: 2026-04-05

- [x] T3. EventRepository 구현
  - 목적: eventType/active 필터 쿼리
  - 완료 기준: 필터 테스트 통과
  - 완료일: 2026-04-05

- [x] T4. EventService 구현
  - 목적: 조회 위임, EVENT_NOT_FOUND 예외
  - 완료 기준: 서비스 테스트 통과
  - 완료일: 2026-04-05

- [x] T5. EventController + DTO 구현
  - 목적: GET /api/events, GET /api/events/{id}
  - 완료 기준: 컨트롤러 테스트 통과
  - 완료일: 2026-04-05

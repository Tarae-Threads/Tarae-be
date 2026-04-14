# EventScheduler 제거 및 active 의미 재정의 마이그레이션

- 작성일: 2026-04-14
- 관련 기능: B-13 관리자 장소/이벤트 CRUD

## 배경

기존에는 `EventScheduler` 가 매일 02시 만료된 이벤트(`end_date < today`)를 `active = false` 로 토글했다.
이로 인해 `active` 컬럼은 두 가지 의미가 혼재되어 있었다.

1. 운영자가 의도적으로 내린 비공개 상태
2. 시스템이 자동 부여한 만료 상태

B-13 관리자 CRUD에서 활성/비활성 토글을 수동 기능으로 노출하면서, 이 두 의미를 분리해야 했다.

## 결정

- `active` 의 의미를 **운영자 수동 공개 플래그**로 한정한다.
- "만료 여부"는 컬럼으로 저장하지 않고, 쿼리 시 `end_date < today` 로 계산한다.
- `EventScheduler` 와 `EventRepository.deactivateExpiredEvents()` 를 제거한다.
- 공개 API는 `active = true AND (end_date IS NULL OR end_date >= today)` 를 강제한다.
- 관리자 페이지는 필터(`AdminEventStatusFilter.ALL/ONGOING/EXPIRING_SOON/EXPIRED`)로 만료 이벤트도 조회 가능하다.

원칙: **계산 가능한 값(파생 상태)은 컬럼으로 저장하지 않는다.**

## 운영 DB 마이그레이션

기존 `events.active = false` 레코드 중 일부는 운영자가 수동으로 내린 것이지만,
대부분은 스케줄러가 자동 비활성화한 것이다.
재배포 후에는 만료 여부가 쿼리로 자동 처리되므로 모든 자동 비활성 레코드를 복원해야 한다.

운영 환경 적용 시점에 다음 SQL 을 1회 수동 실행한다.

```sql
-- 1. 백업 (롤백 대비)
CREATE TABLE events_backup_20260414 AS SELECT id, active FROM events WHERE active = false;

-- 2. 모든 이벤트를 active = true 로 복원
UPDATE events SET active = true WHERE active = false;
```

운영자가 진짜로 내려야 할 이벤트가 있었다면, 배포 후 관리자 페이지(/admin/events) 에서 다시 토글한다.

## 영향 범위

| 영역 | 변경 |
|------|------|
| `EventService.getEvents` | `active=true AND endDate 유효` 강제 |
| `EventService.getEvent` | 같은 조건 위반 시 404 |
| `PlaceService.getPlace` 의 events | `findPublicEventsByPlaceId(id, today)` 사용 |
| 만료 이벤트 자동 비활성화 | **삭제** |
| 관리자 페이지 | 만료 이벤트도 ALL/EXPIRED 필터로 조회 가능 |

## 롤백 절차

1. `events_backup_20260414` 에서 원래 비활성 레코드를 복원
2. 코드 롤백 (`EventScheduler` 복구)

단, `active` 의 의미가 다시 혼재되므로 권장하지 않는다.

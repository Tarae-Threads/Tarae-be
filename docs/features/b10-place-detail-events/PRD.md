# PRD: 장소 상세 조회 응답에 활성 이벤트 목록 포함
- 작성일: 2026-04-11
- 상태: Draft

## 배경 & 목적

장소 상세 화면에서 해당 장소와 연결된 이벤트(팝업, 세일, 테스터 모집 등)를 함께 보여줄 수 있어야 한다.
현재 `GET /api/places/{id}` 응답에는 이벤트 정보가 없어, 프론트에서 별도 API를 추가로 호출해야 하는 비효율이 생긴다.

## 요구사항

### 기능 요구사항
- [ ] 장소 상세 응답(`PlaceDetailResponse`)에 `events` 필드 추가
- [ ] `events`에는 해당 장소에 연결된 이벤트 중 `active = true`인 것만 포함
- [ ] 이벤트가 없는 장소는 `events: []` 반환 (null 금지)
- [ ] 이벤트 항목 포함 필드: `id`, `title`, `eventType`, `startDate`, `endDate`, `active`, `links`

### 비기능 요구사항
- N+1 쿼리 없이 단일 조회로 처리

## 가정 & 미확정 사항
- 가정: `active = true`인 이벤트만 노출 (종료된 이벤트 이력 불필요)
- 미확정: 종료된 이벤트 이력도 보여줄지 여부 → 프론트 개발자 확인 필요 (`docs/collab-notes.md` 참고)

## 범위 외
- 이벤트 목록 API(`GET /api/events`) 변경 없음
- `endDate` 기준 필터링 없음 (active 필드만 사용)

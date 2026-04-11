# TASKS: 장소 상세 조회 응답에 활성 이벤트 목록 포함

- [x] T1. EventRepository에 placeId + active 조회 메서드 추가 (완료일: 2026-04-11)
  - 목적: PlaceService에서 해당 장소의 활성 이벤트를 조회할 수 있도록
  - 작업 내용: `findAllByPlaceIdAndActiveTrue(placeId: Long): List<Event>` 추가
  - 예상 변경 파일: `event/repository/EventRepository.kt`
  - 완료 기준: 메서드 정의 + 단위 테스트 통과

- [x] T2. PlaceEventInfo DTO 추가 (완료일: 2026-04-11)
  - 목적: 장소 상세 응답에 포함될 이벤트 요약 정보 표현
  - 작업 내용: `PlaceEventInfo(id, title, eventType, startDate, endDate, active, links)` 정의, `Event.toPlaceEventInfo()` 확장 함수
  - 실제 변경 파일: `place/dto/PlaceInfo.kt`
  - 완료 기준: DTO 정의 완료

- [x] T3. PlaceDetailResponse에 events 필드 추가 (완료일: 2026-04-11)
  - 목적: 응답 DTO에 이벤트 목록 필드 포함
  - 작업 내용: `events: List<PlaceEventInfo>` 필드 추가, `from()` 팩토리 메서드 시그니처 변경
  - 예상 변경 파일: `place/dto/PlaceDetailResponse.kt`
  - 완료 기준: 기존 테스트 컴파일 통과

- [x] T4. PlaceService에서 이벤트 조회 후 응답에 포함 (완료일: 2026-04-11)
  - 목적: 장소 상세 조회 시 활성 이벤트를 함께 담아 반환
  - 작업 내용: `getPlace()` 내에서 `EventRepository.findAllByPlaceIdAndActiveTrue()` 호출 후 `PlaceDetailResponse`에 전달
  - 예상 변경 파일: `place/service/PlaceService.kt`
  - 완료 기준: 단위 테스트 3케이스 통과 (이벤트 있음 / 없음 / active=false 제외)

- [x] T5. PlaceController 테스트 업데이트 (완료일: 2026-04-11)
  - 목적: 컨트롤러 레이어에서 events 필드 응답 검증
  - 작업 내용: 기존 상세 조회 테스트에 `events` 필드 JSON 검증 추가
  - 예상 변경 파일: `place/controller/PlaceControllerTest.kt`
  - 완료 기준: 컨트롤러 테스트 통과

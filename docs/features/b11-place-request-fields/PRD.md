# PRD: 제보 폼 필드 확장
- 작성일: 2026-04-12
- 상태: Done
- 완료일: 2026-04-12

## 배경 & 목적

현재 장소 제보(PlaceRequest) 폼에서:
- 카테고리는 ID 리스트만 받고, 직접 입력(텍스트)은 불가
- 브랜드(실/바늘/부자재)는 텍스트만 받고, 선택 목록(ID 리스트)은 불가
- PATTERNBOOK(도안/패턴북) 브랜드 타입이 추가됐으나 제보 폼에 미반영

사용자가 앱에서 목록 선택 + 직접 입력을 모두 할 수 있도록 필드를 확장한다.

## 요구사항

### 기능 요구사항

- [ ] 카테고리 직접 입력 텍스트 필드 추가 (`categoryText`)
- [ ] 실 브랜드 ID 리스트 필드 추가 (`brandYarnIds`)
- [ ] 바늘 브랜드 ID 리스트 필드 추가 (`brandNeedleIds`)
- [ ] 부자재 브랜드 ID 리스트 필드 추가 (`brandNotionsIds`)
- [ ] 도안/패턴북 브랜드 필드 추가 — ID 리스트(`brandPatternbookIds`) + 텍스트(`brandsPatternbook`)
- [ ] 기존 텍스트 필드(`brandsYarn`, `brandsNeedle`, `brandsNotions`) 유지 (호환성)

### 비기능 요구사항

- 모든 새 필드는 optional (기존 요청에 영향 없음)
- ID 리스트는 JSON 컬럼으로 저장 (기존 `LongListJsonConverter` 재사용)

## 가정 & 미확정 사항

- 가정: `brandYarnIds` 등의 ID는 `brands` 테이블의 PK를 참조하되, FK 제약은 걸지 않음 (제보는 검증 없이 수집하는 구조)
- 가정: 텍스트와 ID 리스트는 동시에 입력 가능 (앱에서 중복 제어)

## 범위 외

- 관리자 페이지 제보 상세 UI 수정
- 제보 승인 시 Brand ID 유효성 검증

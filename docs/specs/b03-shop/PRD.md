# PRD: 온라인샵 도메인

> 상태: 승인됨
> 작성일: 2026-05-02

## 배경 및 목적

뜨개 관련 온라인 쇼핑몰 정보를 제공하는 도메인. 오프라인 장소(place)와 달리 위치 정보 없이 링크/태그/카테고리/브랜드로 탐색하는 구조.

## 사용자 스토리

- 사용자로서, 온라인샵 목록을 카테고리/태그/키워드로 검색하여 원하는 뜨개 쇼핑몰을 찾을 수 있다.
- 사용자로서, 온라인샵 상세 페이지에서 인스타/네이버/웹사이트 링크로 바로 이동할 수 있다.
- 사용자로서, 온라인샵에 리뷰를 남길 수 있다.
- 사용자로서, 새로운 온라인샵 또는 정보 수정을 제보할 수 있다.
- 관리자로서, 온라인샵을 직접 등록/수정/비활성화/삭제할 수 있다.
- 관리자로서, 제보를 검토하여 승인/거절할 수 있다.

## 요구사항

### 필수 (Must Have)

**온라인샵 엔티티**
- [x] 이름 (name)
- [x] 인스타그램 링크 (instagramUrl)
- [x] 네이버 스마트스토어 링크 (naverUrl)
- [x] 웹사이트 링크 (websiteUrl)
- [x] 카테고리 매핑 (다대다, 기존 Category 공유)
- [x] 태그 매핑 (다대다, 기존 Tag 공유)
- [x] 브랜드 매핑 (다대다, 기존 Brand 공유)
- [x] 활성화 여부 (active)

**공개 API**
- [x] 온라인샵 목록 조회 (키워드/카테고리/태그 필터)
- [x] 온라인샵 상세 조회

**리뷰**
- [x] 기존 Review 도메인 재사용 (ReviewTargetType.SHOP 추가)

**제보 (ShopRequest)**
- [x] 신규 등록 제보 (NEW)
- [x] 정보 수정 제보 (UPDATE)
- [x] 상태 관리 (PENDING → APPROVED / REJECTED)

**관리자 페이지**
- [x] 온라인샵 목록 (페이징, 검색)
- [x] 온라인샵 신규 등록 / 수정 / 삭제 / 활성화토글
- [x] 제보 목록 및 승인/거절

## 제외 범위

- place의 위치 관련 필드 (region, district, address, lat, lng, hoursText, closedDays) 없음
- PlaceStatus (OPEN/CLOSED/RELOCATED) 없음 — active 단일 플래그로만 관리
- place와 Category/Tag/Brand를 공유 (별도 Shop 전용 마스터데이터 생성 안 함)
- 이벤트 도메인과의 연계 없음

## 설계 결정

- 매핑 테이블은 별도 (shop_categories, shop_tags, shop_brands)
- 마스터데이터(categories, tags, brands)는 place와 공유

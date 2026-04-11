# PRD: Place @ManyToMany → 중간 엔티티 전환
- 작성일: 2026-04-11
- 상태: Draft

## 배경 & 목적

Place-Category, Place-Tag, Place-Brand 관계가 현재 `@ManyToMany`로 구현되어 있다.
이 방식은 다음 문제를 가진다:

1. **중간 테이블 확장 불가** — 나중에 display_order, note 같은 컬럼을 추가할 수 없음
2. **삭제 비효율** — categories 교체 시 DELETE ALL + INSERT ALL 발생
3. **QueryDSL 가독성** — 중간 테이블을 직접 다루지 못해 join 경로가 불명확
4. **도메인 메서드 부재** — `place.categories.addAll()` 같은 컬렉션 직접 조작이 외부에 노출됨

명시적 중간 엔티티(PlaceCategory, PlaceTag, PlaceBrand)로 전환하여 위 문제를 해결하고,
연관 코드(AdminRequestService, QueryDSL)도 함께 개선한다.

## 요구사항

### 기능 요구사항
- [ ] Place-Category, Place-Tag, Place-Brand 관계를 중간 엔티티로 관리한다
- [ ] Place 엔티티에 `addCategory()`, `addTag()`, `addBrand()` 도메인 메서드를 제공한다
- [ ] Place 엔티티에 `val categories`, `val tags`, `val brands` 편의 프로퍼티를 제공하여 기존 DTO 코드가 변경 없이 동작한다
- [ ] AdminRequestService에서 도메인 메서드를 사용하여 관계를 설정한다
- [ ] 기존 API 스펙(요청/응답 형식)은 변경되지 않는다
- [ ] 기존 DB 데이터가 보존된다

### 비기능 요구사항
- 전체 테스트 통과
- 마이그레이션 SQL은 기존 데이터 손실 없이 적용 가능해야 함
- API 동작 변경 없음 (순수 내부 리팩토링)

## 가정 & 미확정 사항
- 가정: 현재 place_categories, place_tags, place_brands 테이블의 PK가 복합키(place_id, category_id 등)임
- 가정: 중간 테이블에 추가 컬럼(display_order 등)은 이번 작업 범위 외 (구조만 전환)
- 가정: Flyway 수동 적용 방식 유지

## 범위 외
- display_order, note 등 중간 테이블 추가 컬럼
- Category, Tag, Brand 엔티티 자체 변경
- API 스펙 변경
- Event 도메인 (Brand 연결 없음)

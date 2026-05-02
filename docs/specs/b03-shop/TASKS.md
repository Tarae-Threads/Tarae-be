# TASKS: 온라인샵 도메인

> 연관 SDD: docs/specs/b03-shop/SDD.md
> 시작일: 2026-05-02
> 완료일: 2026-05-02

## Task 목록

### Phase 1 — DB 마이그레이션
- [x] T1. V8 마이그레이션 작성 — shops, shop_categories, shop_tags, shop_brands, shop_requests 테이블 생성 + reviews.target_type ENUM에 SHOP 추가 (완료일: 2026-05-02)

### Phase 2 — 도메인/엔티티
- [x] T2. Shop 엔티티 구현 (Shop, ShopCategory, ShopTag, ShopBrand) (완료일: 2026-05-02)
- [x] T3. ShopRequest 엔티티 구현 (PlaceRequest 패턴 동일) (완료일: 2026-05-02)

### Phase 3 — Repository
- [x] T4. ShopRepository + ShopRepositoryCustom/Impl (QueryDSL, 필터링) (완료일: 2026-05-02)
- [x] T5. ShopRequestRepository 구현 (완료일: 2026-05-02)

### Phase 4 — 공개 API
- [x] T6. ShopService 구현 (목록/상세 조회) (완료일: 2026-05-02)
- [x] T7. ShopController 구현 (GET /api/shops, GET /api/shops/{id}) (완료일: 2026-05-02)
- [x] T8. ReviewTargetType.SHOP 추가 + ReviewService/ReviewController에 shop 엔드포인트 연결 (완료일: 2026-05-02)
- [x] T9. RequestService + RequestController에 POST /api/requests/shops 추가 (완료일: 2026-05-02)

### Phase 5 — 관리자 페이지
- [x] T10. ShopAssociationSyncer 구현 (PlaceAssociationSyncer 패턴 동일) (완료일: 2026-05-02)
- [x] T11. AdminShopService 구현 (목록/폼/CRUD/toggleActive) (완료일: 2026-05-02)
- [x] T12. AdminShopController + Thymeleaf 템플릿 (목록, 신규, 수정 폼) (완료일: 2026-05-02)
- [x] T13. AdminShopRequestService + AdminShopRequestController + Thymeleaf 템플릿 (완료일: 2026-05-02)

### Phase 6 — 문서
- [x] T14. docs/architecture/overview.md 업데이트 (shop 도메인 추가) (완료일: 2026-05-02)

## 완료 기준 (Definition of Done)

- [x] 모든 테스트 통과
- [x] GET /api/shops 목록/필터 조회 동작
- [x] GET /api/shops/{id} 상세 조회 동작
- [x] 리뷰 작성/조회/삭제 동작 (ReviewTargetType.SHOP)
- [x] 제보 등록 동작 (POST /api/requests/shops)
- [x] 관리자 페이지 CRUD 동작
- [x] 관리자 제보 승인/거절 동작
- [x] docs/architecture/overview.md 업데이트

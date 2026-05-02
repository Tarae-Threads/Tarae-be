# SDD: 온라인샵 도메인

> 상태: 완료
> 작성일: 2026-05-02
> 연관 PRD: docs/specs/b03-shop/PRD.md

## 컴포넌트/모듈 구조

```
com.taraethreads.tarae.
├── shop/
│   ├── domain/
│   │   ├── Shop.kt                  (메인 엔티티)
│   │   ├── ShopCategory.kt          (중간 엔티티)
│   │   ├── ShopTag.kt               (중간 엔티티)
│   │   └── ShopBrand.kt             (중간 엔티티)
│   ├── dto/
│   │   ├── ShopListResponse.kt
│   │   ├── ShopDetailResponse.kt
│   │   └── ShopSubDtos.kt
│   ├── repository/
│   │   ├── ShopRepository.kt
│   │   ├── ShopRepositoryCustom.kt
│   │   └── ShopRepositoryImpl.kt
│   ├── service/
│   │   └── ShopService.kt
│   └── controller/
│       └── ShopController.kt
├── request/
│   ├── domain/
│   │   └── ShopRequest.kt           (기존 PlaceRequest 패턴 동일)
│   ├── dto/
│   │   └── ShopRequestInput.kt
│   └── repository/
│       └── ShopRequestRepository.kt
├── review/
│   └── domain/
│       └── ReviewTargetType.kt      (SHOP 추가)
└── admin/
    ├── controller/
    │   ├── AdminShopController.kt
    │   └── AdminShopRequestController.kt
    ├── service/
    │   ├── AdminShopService.kt
    │   └── ShopAssociationSyncer.kt
    └── dto/
        ├── ShopCreateForm.kt
        ├── ShopListRow.kt
        └── ShopBulkCreateRequest.kt
```

## 데이터 흐름

```
[공개 API]
GET /api/shops?keyword=&categoryId=&tagId=
  → ShopController → ShopService → ShopRepositoryImpl (QueryDSL)
  → List<ShopListResponse>

GET /api/shops/{id}
  → ShopController → ShopService → ShopRepository
  → ShopDetailResponse

POST /api/shops/{shopId}/reviews
  → ReviewController → ReviewService (기존, validateTargetExists에 SHOP 추가)

POST /api/shop-requests
  → ShopRequestController → ShopRequestService
  → ShopRequest(PENDING)

[관리자]
GET  /admin/shops
POST /admin/shops
GET  /admin/shops/{id}/edit
POST /admin/shops/{id}
POST /admin/shops/{id}/toggle-active
POST /admin/shops/{id}/delete
  → AdminShopController → AdminShopService → ShopAssociationSyncer

GET  /admin/shop-requests
POST /admin/shop-requests/{id}/approve
POST /admin/shop-requests/{id}/reject
  → AdminShopRequestController → AdminShopRequestService
```

## API 설계

### 공개 API

| Method | Path | 설명 |
|--------|------|------|
| GET | /api/shops | 온라인샵 목록 조회 |
| GET | /api/shops/{id} | 온라인샵 상세 조회 |
| POST | /api/shops/{shopId}/reviews | 리뷰 작성 |
| GET | /api/shops/{shopId}/reviews | 리뷰 목록 |
| POST | /api/shop-requests | 제보 등록 |

### 요청/응답 타입

```
ShopListResponse {
  id: Long
  name: String
  instagramUrl: String?
  naverUrl: String?
  websiteUrl: String?
  categories: List<CategoryDto>
  tags: List<TagDto>
  brands: List<BrandDto>
  active: Boolean
}

ShopDetailResponse {
  // ShopListResponse와 동일 (place와 달리 events 없음)
}

ShopRequestInput {
  requestType: RequestType          // NEW | UPDATE
  shopId: Long?                     // UPDATE일 때만
  name: String?
  instagramUrl: String?
  naverUrl: String?
  websiteUrl: String?
  categoryIds: List<Long>
  categoryText: String?
  tags: String?                     // 쉼표 구분 텍스트 (PlaceRequest와 동일)
  brandYarnIds: List<Long>
  brandsYarn: String?
  brandNeedleIds: List<Long>
  brandsNeedle: String?
  brandNotionsIds: List<Long>
  brandsNotions: String?
  brandPatternbookIds: List<Long>
  brandsPatternbook: String?
  note: String?
}
```

## DB 스키마 변경

### 신규 테이블 (V8)

```sql
CREATE TABLE shops (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(100) NOT NULL,
  instagram_url VARCHAR(255),
  naver_url     VARCHAR(255),
  website_url   VARCHAR(255),
  active     BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL
);

CREATE TABLE shop_categories (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  shop_id     BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  UNIQUE KEY uk_shop_category (shop_id, category_id),
  FOREIGN KEY (shop_id)     REFERENCES shops(id),
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE shop_tags (
  id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  shop_id BIGINT NOT NULL,
  tag_id  BIGINT NOT NULL,
  UNIQUE KEY uk_shop_tag (shop_id, tag_id),
  FOREIGN KEY (shop_id) REFERENCES shops(id),
  FOREIGN KEY (tag_id)  REFERENCES tags(id)
);

CREATE TABLE shop_brands (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  shop_id  BIGINT NOT NULL,
  brand_id BIGINT NOT NULL,
  UNIQUE KEY uk_shop_brand (shop_id, brand_id),
  FOREIGN KEY (shop_id)  REFERENCES shops(id),
  FOREIGN KEY (brand_id) REFERENCES brands(id)
);

CREATE TABLE shop_requests (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_type  ENUM('NEW','UPDATE') NOT NULL,
  shop_id       BIGINT,
  name          VARCHAR(100),
  instagram_url VARCHAR(255),
  naver_url     VARCHAR(255),
  website_url   VARCHAR(255),
  category_ids  JSON,
  category_text VARCHAR(255),
  tags          VARCHAR(500),
  brand_yarn_ids        JSON,
  brands_yarn           VARCHAR(255),
  brand_needle_ids      JSON,
  brands_needle         VARCHAR(255),
  brand_notions_ids     JSON,
  brands_notions        VARCHAR(255),
  brand_patternbook_ids JSON,
  brands_patternbook    VARCHAR(255),
  note          TEXT,
  status        ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  created_at    DATETIME(6) NOT NULL,
  updated_at    DATETIME(6) NOT NULL
);
```

### 기존 테이블 변경 (V8 포함)

```sql
-- ReviewTargetType ENUM에 SHOP 추가 (reviews 테이블)
ALTER TABLE reviews MODIFY COLUMN target_type ENUM('PLACE','EVENT','SHOP') NOT NULL;
```

## 에러 처리 전략

| 에러 코드 | 상황 |
|-----------|------|
| SHOP_NOT_FOUND | 존재하지 않는 shopId |
| SHOP_REQUEST_NOT_FOUND | 존재하지 않는 shopRequestId |

## 기술적 결정 사항

1. **PlaceStatus 없음** — 온라인샵은 active 단일 플래그로 충분. 폐업/이전 개념이 없음.
2. **ReviewTargetType.SHOP 추가** — 기존 Review 인프라 재사용. ReviewService.validateTargetExists에 SHOP 분기 추가.
3. **ShopAssociationSyncer 별도 생성** — PlaceAssociationSyncer와 구조 동일하나 Shop 전용으로 분리.
4. **PlaceRequest와 ShopRequest 별도 엔티티** — 공유하면 NULL 컬럼이 많아지고 도메인 경계가 흐려짐.
5. **제보 구조 PlaceRequest와 동일** — 태그는 텍스트(쉼표 구분)만 받음.

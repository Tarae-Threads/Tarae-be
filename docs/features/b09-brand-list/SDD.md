# SDD: 브랜드 목록 조회 API

## 기술 스택
- Kotlin + Spring Boot
- Spring Data JPA
- Springdoc OpenAPI (Swagger)

## 아키텍처

```
GET /api/brands
    → BrandController
    → BrandService.getBrandsGroupedByType()
    → BrandRepository.findAll()
    → BrandGroupResponse 반환
```

## API 설계

| Method | URL | Request | Response | 설명 |
|--------|-----|---------|----------|------|
| GET | `/api/brands` | — | `BrandGroupResponse` | 타입별 브랜드 목록 |

### Response 예시

```json
{
  "data": [
    {
      "type": "YARN",
      "brands": [
        { "id": 1, "name": "리치모어" },
        { "id": 2, "name": "다루마" }
      ]
    },
    {
      "type": "NEEDLE",
      "brands": [
        { "id": 3, "name": "클로버" }
      ]
    }
  ]
}
```

## DTO 설계

```
BrandGroupResponse
└── data: List<BrandTypeGroup>
    ├── type: String
    └── brands: List<BrandItem>
        ├── id: Long
        └── name: String
```

## 예외 처리

| 상황 | 처리 |
|------|------|
| 브랜드 없음 | 빈 리스트 반환 (예외 아님) |

## 테스트 전략
- 단위 테스트: `BrandServiceTest` — 그룹핑 로직 검증 (MockK)
- 통합 테스트: `BrandControllerTest` — API 응답 구조 검증 (`@SpringBootTest`)

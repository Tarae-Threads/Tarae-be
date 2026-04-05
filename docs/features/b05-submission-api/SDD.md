# SDD: B-05 제보 API
- 작성일: 2026-04-05
- 완료일: 2026-04-05
- 상태: Done

## 패키지 구조

```
com.taraethreads.tarae.
└── request/
    ├── controller/
    │   └── RequestController.kt
    ├── service/
    │   └── RequestService.kt
    └── dto/
        ├── PlaceRequestInput.kt
        ├── EventRequestInput.kt
        └── RequestResponse.kt
```

## API 설계

### 장소 등록 요청

```
POST /api/requests/places
Content-Type: application/json
```

**Request Body** (`PlaceRequestInput`)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| requestType | RequestType | Y | NEW / UPDATE |
| placeId | Long | N | UPDATE일 때 대상 장소 id |
| name | String | N | |
| address | String | N | |
| addressDetail | String | N | |
| lat | BigDecimal | N | |
| lng | BigDecimal | N | |
| categoryIds | List<Long> | N | |
| hoursText | String | N | |
| closedDays | String | N | |
| brandsYarn | String | N | 쉼표 구분 |
| brandsNeedle | String | N | 쉼표 구분 |
| brandsNotions | String | N | 쉼표 구분 |
| instagramUrl | String | N | |
| websiteUrl | String | N | |
| naverMapUrl | String | N | |
| tags | String | N | 쉼표 구분 |
| note | String | N | |

**Response** `201 Created`
```json
{ "id": 1 }
```

### 이벤트 등록 요청

```
POST /api/requests/events
Content-Type: application/json
```

**Request Body** (`EventRequestInput`)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| title | String | Y | |
| eventType | EventType | Y | |
| startDate | LocalDate | Y | |
| endDate | LocalDate | N | |
| locationText | String | N | |
| description | String | N | |

**Response** `201 Created`
```json
{ "id": 1 }
```

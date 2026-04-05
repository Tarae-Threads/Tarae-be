# SDD: 관리자 페이지 (Thymeleaf)

## 기술 스택

| 항목 | 선택 | 이유 |
|------|------|------|
| 뷰 엔진 | Thymeleaf | Spring Boot 표준, SSR |
| CSS | 직접 작성 (어스톤 팔레트) | 프론트 디자인 참고, 빌드 없이 |
| Excel 파싱 | Apache POI 5.x | 검증된 Java 라이브러리 |

## 패키지 구조

```
com.taraethreads.tarae.
└── admin/
    ├── controller/
    │   ├── AdminDashboardController
    │   ├── AdminRequestController     (제보 검수)
    │   ├── AdminPlaceController       (장소 CRUD)
    │   ├── AdminEventController       (이벤트 CRUD)
    │   └── AdminImportController      (Excel import)
    └── service/
        ├── AdminRequestService
        ├── AdminPlaceService
        ├── AdminEventService
        └── AdminImportService

src/main/resources/
├── templates/admin/
│   ├── layout/base.html               (공통 레이아웃)
│   ├── dashboard.html
│   ├── requests/
│   │   ├── list.html
│   │   └── detail.html                (제보 원본 + 입력 폼)
│   ├── places/
│   │   ├── list.html
│   │   └── form.html                  (등록/수정 공용)
│   ├── events/
│   │   ├── list.html
│   │   └── form.html
│   └── import/
│       └── index.html
└── static/admin/
    └── admin.css
```

## 엔티티 변경 사항

### PlaceRequest, EventRequest — status 가변화

현재 `val status`는 변경 불가. 도메인 메서드로 상태 전이 필요.

```kotlin
// 변경 전
val status: RequestStatus = RequestStatus.PENDING

// 변경 후
var status: RequestStatus = RequestStatus.PENDING
    private set

fun approve() {
    check(status == RequestStatus.PENDING) { "PENDING 상태에서만 승인 가능" }
    status = RequestStatus.APPROVED
}

fun reject() {
    check(status == RequestStatus.PENDING) { "PENDING 상태에서만 거절 가능" }
    status = RequestStatus.REJECTED
}
```

## 화면 & URL 설계

| Method | URL | 설명 |
|--------|-----|------|
| GET | /admin | 대시보드 |
| GET | /admin/requests | 제보 목록 (type, status 필터) |
| GET | /admin/requests/place/{id} | 장소 제보 상세 + 검수 폼 |
| POST | /admin/requests/place/{id}/approve | 장소 제보 승인 → Place 생성 |
| POST | /admin/requests/place/{id}/reject | 장소 제보 거절 |
| GET | /admin/requests/event/{id} | 이벤트 제보 상세 + 검수 폼 |
| POST | /admin/requests/event/{id}/approve | 이벤트 제보 승인 → Event 생성 |
| POST | /admin/requests/event/{id}/reject | 이벤트 제보 거절 |
| GET | /admin/places | 장소 목록 |
| GET | /admin/places/new | 장소 등록 폼 |
| POST | /admin/places | 장소 등록 |
| GET | /admin/places/{id}/edit | 장소 수정 폼 |
| POST | /admin/places/{id} | 장소 수정 |
| POST | /admin/places/{id}/delete | 장소 삭제 |
| GET | /admin/events | 이벤트 목록 |
| GET | /admin/events/new | 이벤트 등록 폼 |
| POST | /admin/events | 이벤트 등록 |
| GET | /admin/events/{id}/edit | 이벤트 수정 폼 |
| POST | /admin/events/{id} | 이벤트 수정 |
| POST | /admin/events/{id}/delete | 이벤트 삭제 |
| GET | /admin/import | Excel 업로드 페이지 |
| POST | /admin/import/places | 장소 Excel import |
| POST | /admin/import/events | 이벤트 Excel import |

## 제보 검수 화면 상세

```
┌─────────────────────┬─────────────────────┐
│  제보 원본 (읽기전용) │  DB 저장 폼 (편집)   │
│                     │                     │
│  이름: 실의온도       │  이름: [실의온도   ] │
│  주소: 서울 성동구.. │  지역: [서울      ] │
│  인스타: @xxx       │  동네: [성수      ] │
│  기타 메모: ...      │  주소: [         ] │
│                     │  카테고리: [체크박스] │
│                     │  태그: [체크박스]   │
│                     │  ...                │
│                     │  [거절] [승인 저장] │
└─────────────────────┴─────────────────────┘
```

## Excel Import 컬럼 매핑

### 장소 (Places)
| 컬럼 | 필수 | 비고 |
|------|------|------|
| name | ✅ | |
| region | ✅ | |
| district | ✅ | |
| address | ✅ | |
| lat | | |
| lng | | |
| categories | | 쉼표 구분, 이름으로 lookup |
| tags | | 쉼표 구분, 이름으로 lookup |
| brands | | 쉼표 구분, 이름으로 lookup |
| hours_text | | |
| closed_days | | |
| description | | |
| instagram_url | | |
| naver_map_url | | |
| status | | 기본값 OPEN |

### 이벤트 (Events)
| 컬럼 | 필수 | 비고 |
|------|------|------|
| title | ✅ | |
| event_type | ✅ | SALE / EVENT_POPUP / TESTER_RECRUIT |
| start_date | ✅ | YYYY-MM-DD |
| end_date | | |
| location_text | | |
| instagram_url | | |
| website_url | | |
| description | | |

## 의존성 추가 (build.gradle.kts)

```kotlin
implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
implementation("org.apache.poi:poi-ooxml:5.3.0")
```

## 예외 처리

| 상황 | 처리 |
|------|------|
| 이미 APPROVED/REJECTED 제보 재처리 | 에러 메시지 표시 후 리다이렉트 |
| Excel 컬럼 누락/형식 오류 | 해당 행 skip + 실패 목록 표시 |
| 필수 필드 누락 폼 제출 | 폼 유효성 검사 오류 메시지 |

## 테스트 전략

- `AdminRequestService`: 승인/거절 상태 전이 단위 테스트
- `AdminImportService`: Excel 파싱 + DB 저장 단위 테스트 (MockK)
- Controller: `@WebMvcTest`로 화면 렌더링 및 리다이렉트 확인
- Repository 단위 쿼리는 기존 패턴 따름

# b14: 마스터 데이터 관리 (Category / Tag / Brand CRUD)

- 작성일: 2026-04-14
- 상태: Done
- 성격: 작은 피처라 PRD/SDD/TASKS를 하나로 통합

## 배경

현재 Category, Tag, Brand는 시드 데이터로만 관리되고 있어 관리자가 신규 추가/수정을 하려면 개발자가 SQL을 직접 실행해야 한다.
운영 중에 "새 태그/브랜드 추가"는 빈번하게 발생할 수 있으므로 관리자 페이지에서 처리 가능하게 한다.

## 범위

| 마스터 | 추가 | 수정 | 삭제 |
|--------|------|------|------|
| Category | ✅ | ✅ (이름) | ❌ |
| Tag | ✅ | ✅ (이름) | ❌ |
| Brand | ✅ (이름 + 타입) | ✅ (이름 + 타입) | ❌ |

삭제 제외 이유:
- 장소/이벤트와의 연관관계는 장소/이벤트 편집 페이지에서 chip 해제로 처리 가능
- 마스터 삭제는 조인 테이블 정리까지 필요해 위험 → 필요 시 개발자가 SQL로

## Entity 변경

- `Category.name`: `val` → `var`, `rename(name: String)` 도메인 메서드 추가
- `Tag.name`: `val` → `var`, `rename(name: String)` 도메인 메서드 추가
- `Brand.name`, `Brand.type`: `val` → `var`, `update(name, type)` 도메인 메서드 추가

DB 스키마 변경 없음.

## UI

### 경로
- `GET /admin/masters` — 기본 categories 탭
- `GET /admin/masters?type=categories|tags|brands` — 탭 전환

### 레이아웃
- 사이드바에 "마스터 데이터" 메뉴 추가
- 탭 3개 (`.tab` 클래스 재사용): 카테고리 / 태그 / 브랜드
- 각 탭 내부:
  - 상단: 신규 추가 인라인 폼 (이름 + 추가 버튼; 브랜드는 타입 select 추가)
  - 아래: 기존 항목 테이블 (id, 이름, (브랜드는 타입), 수정 버튼)
  - 수정: 행 인라인 편집 또는 간단한 수정 폼으로 전환

### 액션 경로
- `POST /admin/masters/categories` — 생성
- `POST /admin/masters/categories/{id}` — 수정
- `POST /admin/masters/tags` — 생성
- `POST /admin/masters/tags/{id}` — 수정
- `POST /admin/masters/brands` — 생성 (name, type)
- `POST /admin/masters/brands/{id}` — 수정 (name, type)

모두 submit 후 `redirect:/admin/masters?type=<type>`.

## 서비스 확장

기존 `CategoryService`, `TagService`, `BrandService`에 create/update 추가 (삭제는 안 함).

```kotlin
// CategoryService
@Transactional
fun create(name: String): Category

@Transactional
fun rename(id: Long, name: String)
```

Tag, Brand도 동일 패턴. Brand는 `create(name, type)`, `update(id, name, type)`.

- 중복 이름 validation: 이미 있는 이름이면 `CustomException(DUPLICATE_MASTER_NAME)` throw
- 공백 trim + empty 체크

## DTO

| DTO | 용도 |
|-----|------|
| `MasterCreateRequest` | name (공용) |
| `BrandCreateRequest` | name, type (Brand 전용) |
| `BrandUpdateRequest` | name, type |

## Controller

신규: `AdminMasterController`

- `@GetMapping` — 탭별 목록
- 6개 POST 엔드포인트 (create/update × 3종)

## ErrorCode 추가

| 코드 | status | message |
|------|--------|---------|
| DUPLICATE_MASTER_NAME | 400 | 이미 존재하는 이름입니다 |
| CATEGORY_NOT_FOUND | 404 | (이미 있으면 재활용) |
| TAG_NOT_FOUND | 404 | |
| BRAND_NOT_FOUND | 404 | |

## 테스트

- Entity 단위: `rename()`, `update()` 검증
- Service 단위: create 중복 예외, update 정상/404, 공백 예외
- Controller (`@WebMvcTest`): GET 렌더, POST 리다이렉트

## 변경 파일

### 수정
- `place/domain/Category.kt`, `Tag.kt`, `Brand.kt` — var화 + 도메인 메서드
- `place/service/CategoryService.kt`, `TagService.kt`, `BrandService.kt` — create/update 추가 + `@Transactional`
- `admin/service/AdminDashboardService.kt` — 필요 시 통계 (옵션)
- `templates/admin/layout/base.html` — 사이드바 메뉴 추가
- `global/exception/ErrorCode.kt` — DUPLICATE_MASTER_NAME 추가
- `static/admin/admin.css` — 필요 시 소폭 확장

### 신규
- `admin/controller/AdminMasterController.kt`
- `admin/dto/MasterCreateRequest.kt`
- `admin/dto/BrandCreateRequest.kt`
- `admin/dto/BrandUpdateRequest.kt`
- `templates/admin/masters/list.html`
- 테스트 파일들

## 체크리스트

- [x] Entity var + 도메인 메서드
- [x] Service create/update
- [x] ErrorCode 추가
- [x] Entity/Service 테스트
- [x] Controller + 템플릿
- [x] Controller 테스트
- [x] 사이드바 메뉴
- [x] `./gradlew test` 통과
- [ ] 커밋

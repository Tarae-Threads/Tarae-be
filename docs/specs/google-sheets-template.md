# 타래 구글시트 양식

> DB import용 공식 양식.
> 시트 7개로 구성. 컬럼 순서/이름 변경 금지.

---

## 시트 구성

| 시트명 | 역할 |
|--------|------|
| `Places` | 장소 데이터 입력 |
| `Events` | 이벤트 데이터 입력 |
| `Brands` | 브랜드 목록 (DB 연동, 드롭다운 소스) |
| `Categories` | 카테고리 목록 (DB 연동, 드롭다운 소스) |
| `Tags` | 태그 목록 (DB 연동, 드롭다운 소스) |

> `Brands`, `Categories`, `Tags`는 DB 참조 테이블 그 자체.
> 새 브랜드/태그 추가 시 이 시트에 먼저 추가 후 Places에서 선택.

> **Enum 값 (place_status, event_type, brand_type)** 은 별도 시트 없이
> 데이터 확인 → 항목 직접 입력으로 처리. 아래 각 컬럼 설명 참고.

---

## 시트: `Categories`

> DB의 `categories` 테이블. id 컬럼은 DB import 후 채워짐.

| id | name |
|----|------|
| (import 후 채움) | 뜨개샵 |
| (import 후 채움) | 공방 |
| (import 후 채움) | 뜨개카페 |
| (import 후 채움) | 손염색실 |
| (import 후 채움) | 공예용품점 |

---

## 시트: `Tags`

> DB의 `tags` 테이블.

| id | name |
|----|------|
| (import 후 채움) | 주차가능 |
| (import 후 채움) | 반려동물동반 |
| (import 후 채움) | 원데이클래스 |
| (import 후 채움) | 대관가능 |

---

## 시트: `Brands`

> DB의 `brands` 테이블. type은 `_Enums` 시트 드롭다운.

| id | name | type |
|----|------|------|
| (import 후 채움) | 산네스간 | YARN |
| (import 후 채움) | 위컨 | YARN |
| (import 후 채움) | 히야히야 | NEEDLE |
| (import 후 채움) | 클로버 | NEEDLE |
| (import 후 채움) | 다이소 | NOTIONS |

**→ `type` 컬럼: 항목 직접 입력 드롭다운 `YARN, NEEDLE, NOTIONS`**

---

## 시트: `Places`

| 컬럼 | 필수 | 드롭다운 소스 | 설명 |
|------|:----:|--------------|------|
| name | ✅ | | 장소명 |
| region | ✅ | 항목 직접: `서울, 경기, 인천, 부산, 대구, 광주, 대전, 울산, 제주, 강원, 충북, 충남, 전북, 전남, 경북, 경남` | 광역 지역 |
| district | ✅ | | 동네 (예: 성수, 홍대) |
| address | ✅ | | 도로명 주소 |
| lat | | | 위도 (소수점 7자리) |
| lng | | | 경도 (소수점 7자리) |
| category1 | | `Categories!B열` | 카테고리 1 |
| category2 | | `Categories!B열` | 카테고리 2 (복수일 때) |
| tag1 | | `Tags!B열` | 태그 1 |
| tag2 | | `Tags!B열` | 태그 2 |
| tag3 | | `Tags!B열` | 태그 3 |
| brand1 | | `Brands!B열` | 브랜드 1 |
| brand2 | | `Brands!B열` | 브랜드 2 |
| brand3 | | `Brands!B열` | 브랜드 3 |
| hours_text | | | 영업시간 (예: 화~금 11:00-19:00) |
| closed_days | | | 정기 휴무 (예: 월요일) |
| description | | | 장소 설명 |
| instagram_url | | | 인스타그램 URL |
| naver_map_url | | | 네이버 지도 URL |
| status | | 항목 직접: `OPEN, CLOSED, RELOCATED` | 운영 상태 (기본값: OPEN) |

> **카테고리/태그/브랜드**: 쉼표 구분 대신 열(column)로 분리.
> import 시 비어있는 열은 무시.

---

## 시트: `Events`

| 컬럼 | 필수 | 드롭다운 소스 | 설명 |
|------|:----:|--------------|------|
| title | ✅ | | 이벤트 제목 |
| event_type | ✅ | 항목 직접: `SALE, EVENT_POPUP, TESTER_RECRUIT` | 이벤트 타입 |
| start_date | ✅ | | 시작일 (YYYY-MM-DD) |
| end_date | | | 종료일 (YYYY-MM-DD), 당일이면 비움 |
| location_text | | | 장소 텍스트 |
| lat | | | 위도 |
| lng | | | 경도 |
| instagram_url | | | 인스타그램 URL |
| website_url | | | 웹사이트 URL |
| description | | | 상세 설명 |

---

## 드롭다운 설정 방법 (구글시트)

1. 드롭다운 걸 셀/열 선택
2. 상단 메뉴 → **데이터 → 데이터 확인**
3. 기준: **범위** 선택 → 해당 시트 범위 입력
   - 예) region 열: `Regions!A2:A20`
   - 예) event_type 열: `_Enums!B2:B4`
4. 잘못된 데이터: **경고 표시** 선택

---

## DB Import 흐름

```
Brands 시트 → DB (brands 테이블)
Categories 시트 → DB (categories 테이블)
Tags 시트 → DB (tags 테이블)
           ↓
Places 시트 → DB (places + place_categories + place_tags + place_brands)
Events 시트 → DB (events)
```

> 참조 테이블(Brands/Categories/Tags) 먼저 import 후 Places import.

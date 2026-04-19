---
paths: []
---

# 커밋 메시지 컨벤션

## 형식

```
type: 한글 설명
```

한 줄로 끝낼 수 있으면 한 줄. 맥락이 필요하면 본문 추가.

## Type

| type | 용도 | 예시 |
|------|------|------|
| `feat` | 새 기능 | `feat: 장소 목록 조회 API 구현` |
| `fix` | 버그 수정 | `fix: 장소 검색 시 공백 무시 안 되는 문제 수정` |
| `refactor` | 리팩토링 (동작 변경 없음) | `refactor: PlaceService 검색 로직 분리` |
| `test` | 테스트 추가/수정 | `test: PlaceService 단위테스트 추가` |
| `docs` | 문서 변경 | `docs: API 명세 업데이트` |
| `chore` | 빌드, 설정, 의존성 등 | `chore: MockK 의존성 추가` |
| `style` | 포맷팅, 세미콜론 등 | `style: ktlint 적용` |
| `perf` | 성능 개선 | `perf: 장소 검색 쿼리 인덱스 추가` |

## 규칙

- 제목은 50자 이내
- 마침표 없음
- "무엇을 했는지"가 아니라 "왜/무엇이 바뀌었는지" 중심
- feat/fix는 가능하면 도메인 명시: `feat(place): ...`

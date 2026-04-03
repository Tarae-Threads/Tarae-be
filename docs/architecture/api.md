# API 명세

> API 추가/변경 시 자동 업데이트됨. 상세 명세는 Swagger UI 참고.

## Base URL

`/api/v1`

## 엔드포인트 목록

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| - | - | (API 추가 시 업데이트) | - |

## 공통 응답 형식

```json
// 성공
{ "data": { ... } }

// 실패
{
  "code": "MEMBER_NOT_FOUND",
  "message": "존재하지 않는 회원입니다."
}
```

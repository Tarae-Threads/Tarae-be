# 문서 유지보수 규칙

## Task 완료 시 (매번 필수)

Task 구현이 완료되면 반드시 해당 TASKS.md를 업데이트한다.

```markdown
- [x] T1. BaseEntity 구현 (완료일: 2026-04-05)
```

모든 Task가 완료되면 PRD.md, SDD.md의 상태도 업데이트한다.

```markdown
- 완료일: 2026-04-05
- 상태: Done
```

## 구조 변경 시 아키텍처 문서 업데이트

| 변경 종류 | 업데이트 파일 |
|-----------|--------------|
| 새 도메인 패키지 추가 | `docs/architecture/overview.md` → 도메인 섹션 |
| Entity 추가/변경 | `docs/architecture/db.md` → 해당 테이블 |
| API 추가/변경 | 해당 SDD.md → API 설계 섹션 |
| DB 스키마 변경 | `src/main/resources/db/schema.sql` + `docs/architecture/db.md` |

## 협업 노트 업데이트

프론트엔드와 논의/공유가 필요한 사항 발견 시 즉시 `docs/collab-notes.md`에 추가한다.

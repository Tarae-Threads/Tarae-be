---
name: backlog
description: >
  Manage the project backlog. Add, view, or prioritize features.
  Use with: "백로그 추가", "할 일 추가", "백로그 보여줘", "우선순위 정리".
argument-hint: "[view|add <내용>|prioritize]"
allowed-tools:
  - Read
  - Write
---

# 백로그 관리

명령: $ARGUMENTS

## 동작

- `view` — `docs/backlog/backlog.md` 현재 상태 출력
- `add <내용>` — 새 항목 추가 (우선순위 Medium 기본)
- `prioritize` — 논의 후 우선순위 재정렬

## 백로그 파일 형식

```markdown
| ID | 기능 | 설명 | 우선순위 | 스펙 | 상태 |
|----|------|------|----------|------|------|
| B001 | 회원 가입 | 이메일/비밀번호 가입 | High | docs/features/member/ | 대기 |
```

우선순위: High / Medium / Low / Icebox
상태: 대기 / 진행중 / 완료

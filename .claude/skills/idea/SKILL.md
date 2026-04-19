---
name: idea
description: >
  Quick idea memo — save a brief brainstorming note to docs/ideas/.
  For deep interactive discussions, use the product-advisor agent instead.
  Use with: "아이디어 메모", "간단히 적어둬".
argument-hint: "[주제]"
allowed-tools:
  - Read
  - Write
---

# 아이디어 메모

주제: $ARGUMENTS

## 진행 방식

1. 주제에 대해 구조화된 논의 진행
2. 결과를 `docs/ideas/YYYY-MM-DD-{slug}.md`에 저장

## 출력 형식

```markdown
# {주제}
- 날짜: YYYY-MM-DD

## 핵심 아이디어
...

## 구체적 실행 방안
- ...

## 검토할 점 / 리스크
- ...

## 다음 액션
- [ ] ...
```

기술적 의사결정이 포함되면 `docs/decisions/`에도 기록 권고.

---
name: tech-note
description: >
  Capture technical insights or Kopring learning notes from the current session.
  Use when something interesting, non-obvious, or worth remembering happened.
argument-hint: "[주제 힌트 (선택)]"
allowed-tools:
  - Read
  - Write
  - Grep
  - Bash(git log:*)
  - Bash(git diff:*)
---

# 기술 메모 캡처

주제 힌트: $ARGUMENTS

## 진행 방식

1. 현재 세션의 작업 내용 분석
2. 기록할 만한 인사이트 식별 (뻔한 것 제외)
3. 적절한 파일에 저장

## 저장 위치

- **기술 인사이트/문제 해결**: `docs/tech-notes/YYYY-MM-DD-{slug}.md`
- **코프링 개념 학습**: `docs/tech-notes/learning/YYYY-MM-DD-{concept}.md`

## 기술 인사이트 형식

```markdown
# {주제}
- 날짜: YYYY-MM-DD

## 상황
어떤 맥락에서 이 문제/발견이 생겼나

## 핵심 내용
무엇을 발견/해결했나

## 왜 중요한가
이걸 모르면 어떤 문제가 생길 수 있나

## 코드 예시
\`\`\`kotlin
// 실제 사용한 코드
\`\`\`
```

## 코프링 학습 노트 형식

```markdown
# {개념명}
- 날짜: YYYY-MM-DD

## 개념
이게 뭔지 (코프링/코틀린 처음 보는 사람 기준으로 설명)

## 왜 이걸 쓰나
이 방식을 선택하는 이유, 대안과 비교

## Java Spring과 다른 점
Kotlin 특유의 동작 방식, Java에서 달라지는 부분

## 실제 코드
\`\`\`kotlin
// 이 프로젝트에서 사용한 예시
\`\`\`

## 주의할 점
흔한 실수, 함정, 오해하기 쉬운 부분
```

## 기록하면 좋은 것

- 처음엔 A로 했다가 B로 바꾼 이유
- 에러 메시지 보고 찾아낸 비자명한 원인
- 코프링 플러그인/어노테이션의 내부 동작
- 성능 개선 전/후 비교
- 설계 결정에서 고민한 트레이드오프

## 기록하지 않아도 되는 것

- 공식 문서에 그대로 나오는 내용
- `println` → `log.info` 같은 단순 변경
- 스펙 자체 (그건 docs/features/)

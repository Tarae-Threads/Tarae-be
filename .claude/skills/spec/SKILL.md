---
name: spec
description: >
  Use when user wants to implement a new feature, discusses requirements,
  or says things like "만들어보자", "기능 추가하자", "구현하고 싶어", "개발하자".
  Runs PRD→SDD→TASKS workflow. No code is written before user approval.
argument-hint: "[기능명]"
allowed-tools:
  - Read
  - Glob
  - Write
---

# Spec-Driven 개발 워크플로우

기능명: $ARGUMENTS

## 진행 순서

1. **PRD 작성** → `docs/features/{feature}/PRD.md`
2. **SDD 작성** → `docs/features/{feature}/SDD.md`
3. **TASKS 작성** → `docs/features/{feature}/TASKS.md`
4. 사용자 승인 대기
5. 승인된 Task부터 TDD로 구현

## PRD 형식

```markdown
# PRD: {기능명}
- 작성일: YYYY-MM-DD
- 상태: Draft

## 배경 & 목적
왜 이 기능이 필요한가?

## 요구사항
### 기능 요구사항
- [ ] ...

### 비기능 요구사항
- ...

## 가정 & 미확정 사항
- 가정: ...
- 미확정: ... (→ 확인 필요)

## 범위 외
- ...
```

## SDD 형식

```markdown
# SDD: {기능명}

## 기술 스택
- ...

## 아키텍처
패키지 구조 및 레이어 흐름

## 도메인 모델
주요 Entity, Value Object

## DB 설계
Mermaid ERD + 주요 인덱스

## API 설계
| Method | URL | Request | Response | 설명 |
|--------|-----|---------|----------|------|

## 예외 처리
| 상황 | 예외 | HTTP Status |

## 테스트 전략
- 단위 테스트: ...
- 통합 테스트: ...
```

## TASKS 형식

```markdown
# TASKS: {기능명}

- [ ] Task 제목
  - 목적: 
  - 작업 내용: 
  - 예상 변경 파일: 
  - 완료 기준: 
```

## Task 완료 후 요약

각 Task 구현 완료 후:
- 변경된 파일
- 변경 이유
- 영향 범위
- 다음 Task

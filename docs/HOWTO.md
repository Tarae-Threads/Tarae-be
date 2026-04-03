# Tarae 개발 가이드 — Claude Code 사용법

## 자동으로 일어나는 것들

별도 커맨드 없이 항상 적용됨:

- **TDD**: 기능 구현 시 테스트 먼저 작성 (Red→Green→Refactor)
- **코딩 컨벤션**: .kt 파일 작성 시 rules/ 자동 적용 (val 우선, setter 금지, 메서드 정렬 등)
- **구조도 업데이트**: Entity/API/도메인 변경 시 `docs/architecture/` 자동 갱신
- **기술 메모**: 코프링 새 기능 사용 시 `docs/tech-notes/learning/` 자동 기록
- **인사이트 기록**: 비자명한 문제 해결 시 `docs/tech-notes/` 자동 기록

## 자연어로 자동 발동되는 것들

말하면 알아서 해당 에이전트/스킬이 동작함:

| 이런 말을 하면 | 자동으로 |
|--------------|---------|
| "리뷰해줘", "코드 봐줘", "이거 괜찮아?" | `code-reviewer` 에이전트 — 구조화된 리뷰 리포트 |
| "DB 어떻게 설계해?", "구조 어때?", "아키텍처" | `architect` 에이전트 — 트레이드오프 포함 설계 제안 |
| "이 기능 만들어보자", "기능 추가하자" | `/spec` — PRD→SDD→TASKS 작성 후 승인 대기 |
| "마케팅 얘기하자", "기획", "아이디어" | `/idea` — 브레인스토밍 후 docs/ideas/ 저장 |

## 명시적으로 쓰는 커맨드

| 커맨드 | 용도 |
|--------|------|
| `/spec [기능명]` | 기능 스펙 작성 (PRD→SDD→TASKS) |
| `/backlog view` | 백로그 현황 보기 |
| `/backlog add [내용]` | 백로그 항목 추가 |
| `/backlog prioritize` | 우선순위 재정렬 |
| `/tech-note` | 이번 세션 기술 메모 수동 캡처 |
| `/tech-note [주제]` | 특정 주제로 기술 메모 |
| `/idea [주제]` | 기획/마케팅 브레인스토밍 |

## 개발 플로우

```
1. 아이디어/기획 논의
   → "마케팅 전략 얘기하자" or /idea
   → docs/ideas/에 저장

2. 기능 스펙 작성
   → "회원가입 만들어보자" or /spec 회원가입
   → docs/specs/member/PRD.md → SDD.md → TASKS.md 작성

3. 승인 후 구현
   → "이 Task 진행해줘"
   → TDD로 자동 구현 (테스트 먼저)

4. 코드 리뷰
   → "리뷰해줘"
   → MUST-FIX / SHOULD-FIX / NICE-TO-HAVE 리포트

5. 메모/정리
   → /tech-note (세션 종료 전)
   → /backlog add (다음 할 일 추가)
```

## 문서 구조

```
docs/
├── HOWTO.md              ← 지금 보는 문서
├── architecture/
│   ├── overview.md       ← 레이어/도메인 구조도 (Mermaid)
│   ├── db.md             ← ERD (Mermaid)
│   └── api.md            ← API 엔드포인트 목록
├── specs/{feature}/
│   ├── PRD.md            ← 요구사항
│   ├── SDD.md            ← 기술 설계
│   └── TASKS.md          ← 구현 체크리스트
├── backlog/backlog.md    ← 전체 기능 백로그
├── decisions/            ← 아키텍처 결정 기록 (ADR)
├── tech-notes/           ← 기술 인사이트, 코프링 학습 메모
└── ideas/                ← 기획/마케팅 브레인스토밍
```

## 새 스킬/에이전트 추가 시

이 문서(HOWTO.md)에도 추가해야 함.

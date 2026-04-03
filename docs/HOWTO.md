# Tarae 개발 가이드 — Claude Code 사용법

## 자동으로 일어나는 것들

별도 커맨드 없이 항상 적용됨:

- **TDD**: 기능 구현 시 테스트 먼저 작성 (Red→Green→Refactor)
- **코딩 컨벤션**: .kt 파일 작성 시 rules/ 경로 기반 자동 적용 (val 우선, setter 금지, 메서드 정렬 등)
- **구조도 업데이트**: Entity/API/도메인 변경 시 `docs/architecture/` 자동 갱신
- **기술 메모**: 코프링 새 기능 사용 시 `docs/tech-notes/learning/` 자동 기록
- **인사이트 기록**: 비자명한 문제 해결 시 `docs/tech-notes/` 자동 기록

## 에이전트 (대화형 전문가)

| 이런 말을 하면 | 에이전트 | 모델 | 역할 |
|--------------|---------|------|------|
| "DB 어떻게?", "구조 어때?", "아키텍처" | 🔵 `architect` | opus | 시니어 아키텍트 — 설계 제안 + 트레이드오프 |
| "리뷰해줘", "코드 봐줘", "이거 괜찮아?" | 🔴 `code-reviewer` | sonnet | 까다로운 리뷰어 — 컨벤션 대조 리포트 |
| "기획 얘기하자", "마케팅", "서비스 방향" | 🟣 `product-advisor` | opus | PM/그로스 전략가 — 시장분석 + 전략 논의 |

## 스킬 (워크플로우)

| 커맨드 | 용도 |
|--------|------|
| `/spec [기능명]` | 기능 스펙 작성 (PRD→SDD→TASKS) |
| `/backlog view` | 백로그 현황 보기 |
| `/backlog add [내용]` | 백로그 항목 추가 |
| `/backlog prioritize` | 우선순위 재정렬 |
| `/tech-note` | 이번 세션 기술 메모 캡처 |
| `/idea [주제]` | 간단한 아이디어 메모 (깊은 논의는 product-advisor) |
| `/cc-optimize [audit\|habits]` | Claude Code 세팅 점검/최적화 (글로벌) |

## 개발 플로우

```
1. 기획/마케팅 논의 → product-advisor 에이전트 (깊은 논의)
   or /idea (간단 메모) → docs/ideas/에 저장

2. 기능 스펙 작성 → /spec 기능명 → docs/specs/{feature}/ 생성

3. 설계 논의 → architect 에이전트 → 옵션 비교 + 추천

4. 승인 후 구현 → TDD로 진행 (테스트 먼저)

5. 코드 리뷰 → code-reviewer 에이전트 → MUST-FIX 리포트

6. 세션 마무리 → /tech-note, /backlog add
```

## 문서 구조

```
docs/
├── HOWTO.md                ← 지금 보는 문서
├── architecture/
│   ├── overview.md         ← 레이어/도메인 구조도 (Mermaid)
│   ├── db.md               ← ERD (Mermaid)
│   └── api.md              ← API 엔드포인트 목록
├── specs/{feature}/        ← PRD.md, SDD.md, TASKS.md
├── backlog/backlog.md      ← 전체 기능 백로그
├── decisions/              ← 아키텍처 결정 기록 (ADR)
├── tech-notes/             ← 기술 인사이트
│   └── learning/           ← 코프링 학습 메모
├── ideas/                  ← 기획/마케팅 브레인스토밍
├── collab-notes.md         ← 프론트 개발자 협업 노트
├── issues.md               ← 이슈 기록
└── claude-code-setup-prompt.md ← Claude Code 최적화 프롬프트 (복붙용)
```

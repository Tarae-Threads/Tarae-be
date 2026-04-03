# Claude Code 최적화 프롬프트

> 어디서든 복붙해서 사용. 새 프로젝트나 회사 노트북에서 Claude Code 세팅 점검/최적화할 때 쓸 것.
> 이 프롬프트를 Claude Code에 그대로 붙여넣으면 됨.

---

## 사용법

1. Claude Code 열고 아래 프롬프트 전체를 복붙
2. 결과 보고 수정할 부분 논의
3. 원하면 "이걸 /cc-optimize 스킬로 만들어줘"라고 하면 글로벌 스킬로 저장 가능

---

## 프롬프트 (여기부터 복사)

```
지금부터 이 프로젝트의 Claude Code 환경을 전체 점검하고 최적화해줘.
나는 주니어~미드 레벨 백엔드 개발자이고, Claude Code를 통해 시니어 수준의 코드를 작성하고 싶어.

## 1단계: 현재 환경 수집

아래 파일들을 전부 읽어:

프로젝트 레벨:
- CLAUDE.md
- .claude/settings.json, .claude/settings.local.json
- .claude/agents/*.md (모든 에이전트)
- .claude/skills/*/SKILL.md (모든 스킬)
- .claude/rules/*.md (모든 룰)

글로벌 레벨:
- ~/.claude/settings.json
- ~/.claude/agents/*.md
- ~/.claude/skills/*/SKILL.md

없는 파일은 "미설정"으로 표시.

## 2단계: 체크리스트 점검

### CLAUDE.md
- [ ] 60줄 이하인가?
- [ ] Build/Run 명령어가 있는가?
- [ ] 프로젝트 고유 규칙만 있는가? (코드에서 유추 가능한 건 불필요)
- [ ] "~~하지 마라"가 아닌 "~~대신 ~~해라" 형태인가?
- [ ] 에이전트/스킬 사용 규칙이 명시되어 있는가?
- [ ] 규칙 파일을 @import가 아닌 paths 기반으로 자동 적용하는가?

### 에이전트
- [ ] 강한 페르소나가 있는가? ("너는 ~~한 전문가다")
- [ ] 프로젝트 컨텍스트가 포함되어 있는가?
- [ ] description이 250자 이내인가? (초과 시 잘림)
- [ ] 용도에 맞는 model을 쓰고 있는가? (opus=깊은추론, sonnet=일반, haiku=탐색)
- [ ] color가 설정되어 있는가?
- [ ] tools가 최소 권한으로 제한되어 있는가?

### 스킬
- [ ] 500줄 이내인가?
- [ ] 에이전트와 description 트리거가 겹치지 않는가?
- [ ] 용도에 맞게 user-invocable/disable-model-invocation 설정했는가?

### 훅
- [ ] Stop 훅이 실질적 가치를 제공하는가?
- [ ] 단순 echo만 있지 않은가?

### 설정
- [ ] 자주 쓰는 명령어가 permissions.allow에 있는가?
- [ ] 임시로 추가한 permission이 남아있지 않은가?

## 3단계: 개선안 제시

점검 결과를 아래 형식으로 보여줘:

### 📊 점수: X/10

### ✅ 잘 되어 있는 것
- ...

### 🔴 즉시 수정 필요
| 항목 | 문제 | 개선안 |
|------|------|--------|

### 🟡 개선 추천
| 항목 | 현재 | 제안 |
|------|------|------|

### 💡 추가 제안
- 새로 만들면 좋을 에이전트/스킬/훅

## 4단계: 내 작업 스타일 분석

아래 소스를 전부 분석해서 내 작업 패턴을 파악하고, 자동화할 수 있는 것을 제안해줘:

1. **메모리 파일** — `~/.claude/projects/*/memory/` 전체. 사용자 선호도, 피드백, 프로젝트 상태 등이 있음. 가장 중요한 소스.
2. **git log** (`git log --oneline -30`) — 어떤 종류의 작업을 주로 하는지
3. **docs/ 하위 문서들** — backlog, issues, ideas, tech-notes 등에 축적된 작업 맥락
4. 메모리의 feedback 타입에서 반복 지적 패턴 → 훅으로 자동 방지
5. 메모리의 user 타입에서 선호 방식 → 에이전트/스킬 맞춤

## 5단계: 최신 베스트 프랙티스 반영

아래 자료를 검색해서 내 세팅에 반영할 만한 최신 팁이 있는지 확인해:
- 공식: code.claude.com/docs (agents, hooks, skills, settings)
- GitHub: VoltAgent/awesome-claude-code-subagents, hesreallyhim/awesome-claude-code
- 블로그: alexop.dev claude code guide, blog.sshh.io claude code features

6개월 이상 된 글은 API 변경 가능성 있으니 공식 문서와 대조할 것.

---

분석 끝나면 수정을 직접 해줘. 그리고 이걸 글로벌 스킬로도 저장하고 싶으면
"~/.claude/skills/cc-optimize/SKILL.md로 저장해줘"라고 말해줘.
```

---

## 이 프롬프트의 핵심 포인트

1. **현재 환경 전체 수집** — 빠뜨리는 파일 없이 전부 읽게 함
2. **체크리스트 기반 점검** — 주관적 판단이 아닌 구체적 기준
3. **작업 패턴 분석** — 메모리 + git log + docs 기반 종합 분석
4. **최신 정보 반영** — 공식 문서 + 커뮤니티 검색
5. **스킬화 안내** — 원하면 글로벌 스킬로 저장 가능

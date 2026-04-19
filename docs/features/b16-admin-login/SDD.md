# SDD: 관리자 세션 로그인

## 기술 스택
- Spring Security 6 (Form Login)
- Spring Session (서블릿 기본 세션)
- Thymeleaf (로그인 페이지)
- BCryptPasswordEncoder (기존 빈 재사용)

## 아키텍처

```
Browser
  └── GET /admin/login         → AdminLoginController (로그인 페이지 렌더링)
  └── POST /admin/login        → Spring Security (처리, SecurityConfig에서 설정)
  └── POST /admin/logout       → Spring Security (세션 무효화)
  └── GET /admin/**            → Spring Security 인증 체크 → 미인증 시 /admin/login 리다이렉트
```

### 패키지 변경 내용
```
global/config/
  └── SecurityConfig.kt        (수정 — 핵심 변경)

admin/controller/
  └── AdminLoginController.kt  (신규 — 로그인 페이지 GET만 처리)

resources/templates/admin/
  └── login.html               (신규 — Thymeleaf 로그인 폼)

resources/application.yml      (수정 — 세션 타임아웃)
resources/application-prod.yml (수정 — ADMIN_USERNAME, ADMIN_PASSWORD 환경변수 주입)
```

## SecurityConfig 설계

```kotlin
// 핵심 로직 의사코드
securityFilterChain:
  csrf {
    ignoringRequestMatchers("/api/**")   // API 경로만 CSRF 제외
  }
  authorizeHttpRequests {
    "/admin/login" → permitAll
    "/admin/**"    → authenticated
    "/**"          → permitAll           // API 등 나머지
  }
  formLogin {
    loginPage = "/admin/login"
    loginProcessingUrl = "/admin/login"
    defaultSuccessUrl = "/admin/dashboard"
    failureUrl = "/admin/login?error=true"
  }
  logout {
    logoutUrl = "/admin/logout"
    logoutSuccessUrl = "/admin/login"
    invalidateHttpSession = true
    deleteCookies = "JSESSIONID"
  }
```

## 관리자 계정 관리

`InMemoryUserDetailsManager` 사용 — DB 테이블 불필요.

```kotlin
@Bean
fun userDetailsService(
    @Value("\${admin.username}") username: String,
    @Value("\${admin.password}") rawPassword: String,
): UserDetailsService {
    val user = User.withUsername(username)
        .password(passwordEncoder().encode(rawPassword))
        .roles("ADMIN")
        .build()
    return InMemoryUserDetailsManager(user)
}
```

환경변수 매핑:
```yaml
# application.yml
admin:
  username: ${ADMIN_USERNAME:admin}     # 로컬 기본값
  password: ${ADMIN_PASSWORD:changeme}  # 로컬 기본값
```

## 세션 설정

```yaml
server:
  servlet:
    session:
      timeout: 3h
      cookie:
        http-only: true
        secure: false   # 로컬
```

```yaml
# application-prod.yml
server:
  servlet:
    session:
      cookie:
        secure: true    # 프로덕션 HTTPS
```

## 로그인 페이지 (`login.html`)

- 기존 관리자 UI 스타일에 맞춘 Thymeleaf 템플릿
- `?error=true` 파라미터 있을 때 오류 메시지 표시
- CSRF 토큰 자동 포함 (Thymeleaf + Spring Security 자동 처리)

## 예외 처리

| 상황 | 처리 |
|------|------|
| 아이디/비밀번호 틀림 | `/admin/login?error=true` 리다이렉트 + 오류 메시지 |
| 미인증 `/admin/**` 접근 | `/admin/login` 리다이렉트 |
| 세션 만료 후 접근 | `/admin/login` 리다이렉트 |

## 테스트 전략

- 단위 테스트: SecurityConfig 빈 로드 확인 (Spring Security Test)
- 통합 테스트:
  - 미인증 `/admin/dashboard` 접근 → 302 `/admin/login`
  - 잘못된 비밀번호 → `/admin/login?error=true`
  - 정상 로그인 → 302 `/admin/dashboard`
  - 로그아웃 → 세션 무효화 확인
  - `/api/**` CSRF 미적용 확인

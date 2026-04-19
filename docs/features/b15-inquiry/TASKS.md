# TASKS: 문의하기(inquiry) 기능

- [x] T1. DB 스키마 — inquiries 테이블 생성 (완료일: 2026-04-19)
  - 목적: inquiry 도메인 DB 기반 구축
  - 작업 내용: `src/main/resources/db/migration/` 하위 SQL 파일 작성 후 수동 실행
  - 예상 변경 파일: `db/migration/create_inquiries_table.sql`
  - 완료 기준: inquiries 테이블 및 idx_inquiries_status 인덱스 생성 확인

- [x] T2. Inquiry 도메인 모델 구현 (완료일: 2026-04-19)
  - 목적: inquiry 엔티티 및 상태 값 객체 정의
  - 작업 내용: `InquiryStatus` enum, `Inquiry` 엔티티 (answer/markSent/close 도메인 메서드 포함)
  - 예상 변경 파일: `inquiry/domain/Inquiry.kt`, `inquiry/domain/InquiryStatus.kt`
  - 완료 기준: 단위 테스트 — 상태 전이(answer→SEND_FAILED, markSent→ANSWERED, close→CLOSED), 이중 처리 예외 검증 통과

- [x] T3. ErrorCode 추가 (완료일: 2026-04-19)
  - 목적: 도메인 예외 처리 기반
  - 작업 내용: `INQUIRY_NOT_FOUND`, `INQUIRY_ALREADY_PROCESSED` ErrorCode 추가
  - 예상 변경 파일: `global/exception/ErrorCode.kt`
  - 완료 기준: 컴파일 통과

- [x] T4. 문의 제출 API 구현 (완료일: 2026-04-19)
  - 목적: 사용자가 문의를 제출할 수 있는 공개 API
  - 작업 내용: `InquiryRepository`, `InquiryService`, `InquiryCreateRequest`, `InquiryCreateResponse`, `InquiryController`
  - 예상 변경 파일: `inquiry/repository/`, `inquiry/service/`, `inquiry/dto/`, `inquiry/controller/`
  - 완료 기준: `POST /api/inquiries` 통합 테스트 통과

- [x] T5. MailService 구현 (Gmail SMTP) (완료일: 2026-04-19)
  - 목적: 이메일 발송 인프라 구축
  - 작업 내용: `spring-boot-starter-mail` 의존성 추가, `MailService` 구현, `application.yml` 메일 설정 (환경변수로 주입)
  - 예상 변경 파일: `build.gradle.kts`, `global/mail/MailService.kt`, `application.yml`
  - 완료 기준: 단위 테스트 — `JavaMailSender` Mock으로 발송 메서드 호출 및 예외 전파 검증

- [x] T6. 관리자 문의 관리 구현 (완료일: 2026-04-19)
  - 목적: 관리자가 문의 목록 조회, 답변, 재발송, 닫기를 처리할 수 있는 관리자 페이지
  - 작업 내용:
    - `AdminInquiryService` (reply: answer→이메일발송→markSent, resend, close)
    - `AdminInquiryController` (GET 목록/상세, POST reply/resend/close)
    - Thymeleaf 템플릿 (목록: SEND_FAILED 배지 강조, 상세: 답변 폼 + 재발송 버튼)
  - 예상 변경 파일: `admin/service/AdminInquiryService.kt`, `admin/controller/AdminInquiryController.kt`, `admin/dto/AdminInquiry*.kt`, `templates/admin/inquiries/list.html`, `templates/admin/inquiries/detail.html`
  - 완료 기준: 단위 테스트 — 발송 성공 시 ANSWERED, 발송 실패 시 SEND_FAILED 유지 검증

- [x] T7. 아키텍처 문서 업데이트 (완료일: 2026-04-19)
  - 목적: 도메인 추가 반영
  - 작업 내용: `docs/architecture/overview.md` 도메인 테이블에 inquiry 행 추가
  - 예상 변경 파일: `docs/architecture/overview.md`
  - 완료 기준: inquiry 도메인 항목 추가

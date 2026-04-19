# 이메일 발송과 트랜잭션 타이밍 문제
- 날짜: 2026-04-19

## 상황
`AdminInquiryService.reply()`에서 문의 답변 저장과 이메일 발송을 하나의 `@Transactional` 메서드 안에서 처리하는 구조를 작성했다. 코드리뷰에서 이 구조의 타이밍 문제가 지적됨.

## 핵심 내용
현재 실행 순서:
```
@Transactional reply() 시작
  → inquiry.answer()       // DB: SEND_FAILED (아직 커밋 안 됨)
  → mailService.send()     // 이메일 발송 ← 커밋 전에 나감
  → inquiry.markSent()     // DB: ANSWERED (아직 커밋 안 됨)
reply() 종료 → Spring 커밋
```

문제: 메일이 나간 시점에 DB는 아직 커밋되지 않았다. 메일 발송 성공 후 DB 커밋이 실패하면 사용자는 메일을 받았는데 관리자 페이지엔 PENDING 상태로 남는 불일치가 발생한다.

## 왜 중요한가
외부 시스템 호출(이메일, 결제, 푸시 알림 등)은 항상 DB 커밋 이후에 발생해야 한다. 트랜잭션 내에서 외부 시스템을 호출하면 두 시스템 간 일관성이 깨질 수 있다.

## 이상적인 해결책
Spring의 `@TransactionalEventListener(phase = AFTER_COMMIT)` 패턴:

```kotlin
// 1. 서비스에서 이벤트 발행
@Transactional
fun reply(id: Long, replyBody: String) {
    val inquiry = findById(id)
    inquiry.answer(replyBody)  // SEND_FAILED로 저장
    eventPublisher.publishEvent(InquiryAnsweredEvent(inquiry.id))
}  // 여기서 커밋

// 2. 커밋 완료 후 리스너 실행
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
@Transactional(propagation = Propagation.REQUIRES_NEW)
fun onInquiryAnswered(event: InquiryAnsweredEvent) {
    val inquiry = findById(event.inquiryId)
    try {
        mailService.sendReply(inquiry.email, inquiry.title, inquiry.replyBody!!)
        inquiry.markSent()  // 새 트랜잭션에서 ANSWERED 커밋
    } catch (e: MailException) {
        // SEND_FAILED 유지
    }
}
```

## 현재 결정
서비스 규모가 작고 DB 커밋 실패 확률이 극히 낮아 defer. SEND_FAILED 상태로 관리자가 재발송할 수 있으므로 최악의 경우에도 중복 발송 정도의 문제로 그침.

트래픽 증가, 결제 연동, 중요 알림 추가 시 `@TransactionalEventListener` 패턴으로 개선 필요.

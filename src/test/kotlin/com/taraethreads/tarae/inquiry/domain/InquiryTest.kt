package com.taraethreads.tarae.inquiry.domain

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class InquiryTest {

    private fun pendingInquiry() = Inquiry(
        title = "배송 문의",
        body = "언제 도착하나요?",
        email = "user@example.com",
    )

    @Nested
    inner class `answer 답변 저장` {

        @Test
        fun `PENDING 상태에서 answer 하면 replyBody 저장 후 SEND_FAILED 가 된다`() {
            val inquiry = pendingInquiry()
            inquiry.answer("확인 후 연락드리겠습니다")
            assertThat(inquiry.status).isEqualTo(InquiryStatus.SEND_FAILED)
            assertThat(inquiry.replyBody).isEqualTo("확인 후 연락드리겠습니다")
        }

        @Test
        fun `ANSWERED 상태에서 answer 하면 예외가 발생한다`() {
            val inquiry = pendingInquiry()
            inquiry.answer("답변")
            inquiry.markSent()
            assertThatThrownBy { inquiry.answer("재답변") }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.INQUIRY_ALREADY_PROCESSED)
        }

        @Test
        fun `CLOSED 상태에서 answer 하면 예외가 발생한다`() {
            val inquiry = pendingInquiry()
            inquiry.close()
            assertThatThrownBy { inquiry.answer("답변") }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.INQUIRY_ALREADY_PROCESSED)
        }
    }

    @Nested
    inner class `markSent 발송 성공 처리` {

        @Test
        fun `markSent 하면 ANSWERED 상태가 되고 repliedAt 이 기록된다`() {
            val inquiry = pendingInquiry()
            inquiry.answer("답변")
            inquiry.markSent()
            assertThat(inquiry.status).isEqualTo(InquiryStatus.ANSWERED)
            assertThat(inquiry.repliedAt).isNotNull()
        }
    }

    @Nested
    inner class `resend 재발송 검증` {

        @Test
        fun `SEND_FAILED 상태에서 resend 하면 예외 없이 통과한다`() {
            val inquiry = pendingInquiry()
            inquiry.answer("답변")
            inquiry.resend()
        }

        @Test
        fun `PENDING 상태에서 resend 하면 예외가 발생한다`() {
            val inquiry = pendingInquiry()
            assertThatThrownBy { inquiry.resend() }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.INQUIRY_ALREADY_PROCESSED)
        }

        @Test
        fun `ANSWERED 상태에서 resend 하면 예외가 발생한다`() {
            val inquiry = pendingInquiry()
            inquiry.answer("답변")
            inquiry.markSent()
            assertThatThrownBy { inquiry.resend() }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.INQUIRY_ALREADY_PROCESSED)
        }
    }

    @Nested
    inner class `close 닫기` {

        @Test
        fun `PENDING 상태에서 close 하면 CLOSED 가 된다`() {
            val inquiry = pendingInquiry()
            inquiry.close()
            assertThat(inquiry.status).isEqualTo(InquiryStatus.CLOSED)
        }

        @Test
        fun `SEND_FAILED 상태에서도 close 하면 CLOSED 가 된다`() {
            val inquiry = pendingInquiry()
            inquiry.answer("답변")
            inquiry.close()
            assertThat(inquiry.status).isEqualTo(InquiryStatus.CLOSED)
        }

        @Test
        fun `ANSWERED 상태에서 close 하면 예외가 발생한다`() {
            val inquiry = pendingInquiry()
            inquiry.answer("답변")
            inquiry.markSent()
            assertThatThrownBy { inquiry.close() }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.INQUIRY_ALREADY_PROCESSED)
        }
    }

    @Nested
    inner class `updateMemo 메모 업데이트` {

        @Test
        fun `메모를 저장하면 memo 필드가 업데이트된다`() {
            val inquiry = pendingInquiry()
            inquiry.updateMemo("스팸 의심")
            assertThat(inquiry.memo).isEqualTo("스팸 의심")
        }

        @Test
        fun `빈 문자열로 업데이트하면 memo 가 null 이 된다`() {
            val inquiry = pendingInquiry()
            inquiry.updateMemo("기존 메모")
            inquiry.updateMemo("  ")
            assertThat(inquiry.memo).isNull()
        }

        @Test
        fun `null 로 업데이트하면 memo 가 null 이 된다`() {
            val inquiry = pendingInquiry()
            inquiry.updateMemo("기존 메모")
            inquiry.updateMemo(null)
            assertThat(inquiry.memo).isNull()
        }
    }
}

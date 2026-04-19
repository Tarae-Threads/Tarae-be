package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.global.mail.MailService
import com.taraethreads.tarae.inquiry.domain.Inquiry
import com.taraethreads.tarae.inquiry.domain.InquiryStatus
import com.taraethreads.tarae.inquiry.repository.InquiryRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.mail.MailSendException
import java.util.Optional

class AdminInquiryServiceTest {

    private val inquiryRepository = mockk<InquiryRepository>()
    private val mailService = mockk<MailService>()
    private val adminInquiryService = AdminInquiryService(inquiryRepository, mailService)

    private fun pendingInquiry() = Inquiry(
        title = "배송 문의",
        body = "언제 도착하나요?",
        email = "user@example.com",
    )

    @Nested
    inner class `reply 답변 및 이메일 발송` {

        @Test
        fun `이메일 발송 성공 시 상태가 ANSWERED 가 된다`() {
            // given
            val inquiry = pendingInquiry()
            every { inquiryRepository.findById(1L) } returns Optional.of(inquiry)
            every { mailService.sendReply(any(), any(), any()) } just runs

            // when
            adminInquiryService.reply(1L, "답변 내용입니다")

            // then
            assertThat(inquiry.status).isEqualTo(InquiryStatus.ANSWERED)
            assertThat(inquiry.replyBody).isEqualTo("답변 내용입니다")
            verify { mailService.sendReply("user@example.com", "배송 문의", "답변 내용입니다") }
        }

        @Test
        fun `이메일 발송 실패 시 상태가 SEND_FAILED 로 유지된다`() {
            // given
            val inquiry = pendingInquiry()
            every { inquiryRepository.findById(1L) } returns Optional.of(inquiry)
            every { mailService.sendReply(any(), any(), any()) } throws MailSendException("SMTP 오류")

            // when
            adminInquiryService.reply(1L, "답변 내용입니다")

            // then
            assertThat(inquiry.status).isEqualTo(InquiryStatus.SEND_FAILED)
            assertThat(inquiry.replyBody).isEqualTo("답변 내용입니다")
        }

        @Test
        fun `존재하지 않는 문의 id 로 reply 하면 예외가 발생한다`() {
            // given
            every { inquiryRepository.findById(999L) } returns Optional.empty()

            // when & then
            assertThatThrownBy { adminInquiryService.reply(999L, "답변") }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.INQUIRY_NOT_FOUND)
        }
    }

    @Nested
    inner class `resend 재발송` {

        @Test
        fun `재발송 성공 시 상태가 ANSWERED 가 된다`() {
            // given
            val inquiry = pendingInquiry()
            inquiry.answer("저장된 답변")
            every { inquiryRepository.findById(1L) } returns Optional.of(inquiry)
            every { mailService.sendReply(any(), any(), any()) } just runs

            // when
            adminInquiryService.resend(1L)

            // then
            assertThat(inquiry.status).isEqualTo(InquiryStatus.ANSWERED)
        }

        @Test
        fun `재발송 실패 시 상태가 SEND_FAILED 로 유지된다`() {
            // given
            val inquiry = pendingInquiry()
            inquiry.answer("저장된 답변")
            every { inquiryRepository.findById(1L) } returns Optional.of(inquiry)
            every { mailService.sendReply(any(), any(), any()) } throws MailSendException("SMTP 오류")

            // when
            adminInquiryService.resend(1L)

            // then
            assertThat(inquiry.status).isEqualTo(InquiryStatus.SEND_FAILED)
        }
    }

    @Nested
    inner class `close 닫기` {

        @Test
        fun `PENDING 상태의 문의를 닫으면 CLOSED 가 된다`() {
            // given
            val inquiry = pendingInquiry()
            every { inquiryRepository.findById(1L) } returns Optional.of(inquiry)

            // when
            adminInquiryService.close(1L)

            // then
            assertThat(inquiry.status).isEqualTo(InquiryStatus.CLOSED)
        }

        @Test
        fun `SEND_FAILED 상태의 문의도 닫을 수 있다`() {
            // given
            val inquiry = pendingInquiry()
            inquiry.answer("답변")
            every { inquiryRepository.findById(1L) } returns Optional.of(inquiry)

            // when
            adminInquiryService.close(1L)

            // then
            assertThat(inquiry.status).isEqualTo(InquiryStatus.CLOSED)
        }
    }

    @Nested
    inner class `updateMemo 메모 업데이트` {

        @Test
        fun `메모를 저장하면 inquiry memo 가 업데이트된다`() {
            // given
            val inquiry = pendingInquiry()
            every { inquiryRepository.findById(1L) } returns Optional.of(inquiry)

            // when
            adminInquiryService.updateMemo(1L, "스팸 의심")

            // then
            assertThat(inquiry.memo).isEqualTo("스팸 의심")
        }
    }
}

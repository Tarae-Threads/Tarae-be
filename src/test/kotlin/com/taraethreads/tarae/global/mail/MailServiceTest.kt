package com.taraethreads.tarae.global.mail

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.mail.MailException
import org.springframework.mail.MailSendException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

class MailServiceTest {

    private val mailSender = mockk<JavaMailSender>()
    private val mailService = MailService(mailSender, from = "sender@example.com")

    @Nested
    inner class `sendReply 이메일 발송` {

        @Test
        fun `올바른 수신자와 제목으로 이메일을 발송한다`() {
            // given
            val messageSlot = slot<SimpleMailMessage>()
            every { mailSender.send(capture(messageSlot)) } returns Unit

            // when
            mailService.sendReply(
                to = "user@example.com",
                inquiryTitle = "배송 문의",
                replyBody = "안녕하세요, 확인 후 연락드리겠습니다.",
            )

            // then
            verify { mailSender.send(any<SimpleMailMessage>()) }
            assertThat(messageSlot.captured.to).contains("user@example.com")
            assertThat(messageSlot.captured.subject).contains("배송 문의")
            assertThat(messageSlot.captured.text).isEqualTo("안녕하세요, 확인 후 연락드리겠습니다.")
        }

        @Test
        fun `발송 실패 시 MailException 을 그대로 던진다`() {
            // given
            every { mailSender.send(any<SimpleMailMessage>()) } throws MailSendException("SMTP 오류")

            // when & then
            assertThatThrownBy {
                mailService.sendReply(
                    to = "user@example.com",
                    inquiryTitle = "제목",
                    replyBody = "내용",
                )
            }.isInstanceOf(MailException::class.java)
        }
    }
}

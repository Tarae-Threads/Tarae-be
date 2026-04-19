package com.taraethreads.tarae.global.mail

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val from: String,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun sendReply(to: String, inquiryTitle: String, replyBody: String) {
        val message = SimpleMailMessage().apply {
            setFrom(from)
            setTo(to)
            subject = "[타래] 문의 답변이 도착했습니다 — $inquiryTitle"
            text = replyBody
        }
        try {
            mailSender.send(message)
        } catch (e: MailException) {
            log.error("이메일 발송 실패 [to=$to, subject=${message.subject}]", e)
            throw e
        }
    }
}

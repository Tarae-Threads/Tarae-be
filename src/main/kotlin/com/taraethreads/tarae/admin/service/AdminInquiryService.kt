package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.AdminInquiryDetailDto
import com.taraethreads.tarae.admin.dto.AdminInquiryListRow
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.global.mail.MailService
import com.taraethreads.tarae.inquiry.domain.Inquiry
import com.taraethreads.tarae.inquiry.domain.InquiryStatus
import com.taraethreads.tarae.inquiry.repository.InquiryRepository
import org.slf4j.LoggerFactory
import org.springframework.mail.MailException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminInquiryService(
    private val inquiryRepository: InquiryRepository,
    private val mailService: MailService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun getInquiries(status: InquiryStatus?): List<AdminInquiryListRow> {
        val inquiries = if (status == null) inquiryRepository.findAllByOrderByCreatedAtDesc()
        else inquiryRepository.findAllByStatusOrderByCreatedAtDesc(status)
        return inquiries.map { AdminInquiryListRow.from(it) }
    }

    fun getInquiryDetail(id: Long): AdminInquiryDetailDto =
        AdminInquiryDetailDto.from(findById(id))

    @Transactional
    fun reply(id: Long, replyBody: String) {
        val inquiry = findById(id)
        inquiry.answer(replyBody)
        sendReplyMail(inquiry)
    }

    @Transactional
    fun resend(id: Long) {
        val inquiry = findById(id)
        inquiry.resend()
        sendReplyMail(inquiry)
    }

    @Transactional
    fun close(id: Long) {
        findById(id).close()
    }

    @Transactional
    fun updateMemo(id: Long, memo: String?) {
        findById(id).updateMemo(memo)
    }

    private fun findById(id: Long): Inquiry =
        inquiryRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.INQUIRY_NOT_FOUND) }

    private fun sendReplyMail(inquiry: Inquiry) {
        val replyBody = inquiry.replyBody
            ?: throw CustomException(ErrorCode.INTERNAL_ERROR)
        try {
            mailService.sendReply(
                to = inquiry.email,
                inquiryTitle = inquiry.title,
                replyBody = replyBody,
            )
            inquiry.markSent()
        } catch (e: MailException) {
            log.error("문의 답변 이메일 발송 실패 [inquiryId=${inquiry.id}]", e)
            // 상태는 SEND_FAILED 유지
        }
    }
}

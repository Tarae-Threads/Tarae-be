package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.inquiry.domain.Inquiry
import com.taraethreads.tarae.inquiry.domain.InquiryStatus
import java.time.LocalDateTime

data class AdminInquiryDetailDto(
    val id: Long,
    val title: String,
    val body: String,
    val email: String,
    val status: InquiryStatus,
    val replyBody: String?,
    val repliedAt: LocalDateTime?,
    val memo: String?,
    val createdAt: LocalDateTime,
) {
    val isPending: Boolean get() = status == InquiryStatus.PENDING
    val isSendFailed: Boolean get() = status == InquiryStatus.SEND_FAILED
    val isAnswered: Boolean get() = status == InquiryStatus.ANSWERED
    val isCloseable: Boolean get() = status in setOf(InquiryStatus.PENDING, InquiryStatus.SEND_FAILED)

    companion object {
        fun from(inquiry: Inquiry) = AdminInquiryDetailDto(
            id = inquiry.id,
            title = inquiry.title,
            body = inquiry.body,
            email = inquiry.email,
            status = inquiry.status,
            replyBody = inquiry.replyBody,
            repliedAt = inquiry.repliedAt,
            memo = inquiry.memo,
            createdAt = inquiry.createdAt,
        )
    }
}

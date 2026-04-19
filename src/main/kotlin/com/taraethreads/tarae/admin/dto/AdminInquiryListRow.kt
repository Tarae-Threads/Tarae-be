package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.inquiry.domain.Inquiry
import com.taraethreads.tarae.inquiry.domain.InquiryStatus
import java.time.LocalDateTime

data class AdminInquiryListRow(
    val id: Long,
    val title: String,
    val email: String,
    val status: InquiryStatus,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(inquiry: Inquiry) = AdminInquiryListRow(
            id = inquiry.id,
            title = inquiry.title,
            email = inquiry.email,
            status = inquiry.status,
            createdAt = inquiry.createdAt,
        )
    }
}

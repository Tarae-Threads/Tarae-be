package com.taraethreads.tarae.inquiry.domain

import com.taraethreads.tarae.global.common.BaseEntity
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "inquiries")
class Inquiry(
    @Column(nullable = false, length = 255)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val body: String,

    @Column(nullable = false, length = 100)
    val email: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    var status: InquiryStatus = InquiryStatus.PENDING
        internal set

    @Column(columnDefinition = "TEXT")
    var replyBody: String? = null
        internal set

    var repliedAt: LocalDateTime? = null
        internal set

    @Column(columnDefinition = "TEXT")
    var memo: String? = null
        internal set

    fun answer(replyBody: String) {
        if (status != InquiryStatus.PENDING) throw CustomException(ErrorCode.INQUIRY_ALREADY_PROCESSED)
        this.replyBody = replyBody
        this.status = InquiryStatus.SEND_FAILED
    }

    fun markSent() {
        this.status = InquiryStatus.ANSWERED
        this.repliedAt = LocalDateTime.now()
    }

    fun resend() {
        if (status != InquiryStatus.SEND_FAILED) throw CustomException(ErrorCode.INQUIRY_ALREADY_PROCESSED)
    }

    fun close() {
        if (status !in setOf(InquiryStatus.PENDING, InquiryStatus.SEND_FAILED)) {
            throw CustomException(ErrorCode.INQUIRY_ALREADY_PROCESSED)
        }
        this.status = InquiryStatus.CLOSED
    }

    fun updateMemo(memo: String?) {
        this.memo = memo?.takeIf { it.isNotBlank() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Inquiry) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

package com.taraethreads.tarae.inquiry.repository

import com.taraethreads.tarae.inquiry.domain.Inquiry
import com.taraethreads.tarae.inquiry.domain.InquiryStatus
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryRepository : JpaRepository<Inquiry, Long> {
    fun countByStatus(status: InquiryStatus): Long
    fun findAllByStatusOrderByCreatedAtDesc(status: InquiryStatus): List<Inquiry>
    fun findAllByOrderByCreatedAtDesc(): List<Inquiry>
}

package com.taraethreads.tarae.inquiry.service

import com.taraethreads.tarae.inquiry.domain.Inquiry
import com.taraethreads.tarae.inquiry.dto.InquiryCreateRequest
import com.taraethreads.tarae.inquiry.dto.InquiryCreateResponse
import com.taraethreads.tarae.inquiry.repository.InquiryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InquiryService(
    private val inquiryRepository: InquiryRepository,
) {

    fun create(request: InquiryCreateRequest): InquiryCreateResponse {
        val inquiry = inquiryRepository.save(
            Inquiry(
                title = request.title,
                body = request.body,
                email = request.email,
            )
        )
        return InquiryCreateResponse.from(inquiry)
    }
}

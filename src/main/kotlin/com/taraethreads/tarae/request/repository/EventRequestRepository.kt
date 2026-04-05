package com.taraethreads.tarae.request.repository

import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import org.springframework.data.jpa.repository.JpaRepository

interface EventRequestRepository : JpaRepository<EventRequest, Long> {
    fun countByStatus(status: RequestStatus): Long
    fun findAllByStatus(status: RequestStatus): List<EventRequest>
}

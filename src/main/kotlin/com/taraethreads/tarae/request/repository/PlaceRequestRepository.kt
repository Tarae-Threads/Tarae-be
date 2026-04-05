package com.taraethreads.tarae.request.repository

import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceRequestRepository : JpaRepository<PlaceRequest, Long> {
    fun countByStatus(status: RequestStatus): Long
    fun findAllByStatus(status: RequestStatus): List<PlaceRequest>
}

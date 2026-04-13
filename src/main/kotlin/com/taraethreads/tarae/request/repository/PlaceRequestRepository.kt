package com.taraethreads.tarae.request.repository

import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceRequestRepository : JpaRepository<PlaceRequest, Long> {
    fun countByStatus(status: RequestStatus): Long
    fun findAllByStatusOrderByCreatedAtDesc(status: RequestStatus): List<PlaceRequest>
    fun findAllByRequestTypeOrderByCreatedAtDesc(requestType: RequestType): List<PlaceRequest>
    fun findAllByStatusAndRequestTypeOrderByCreatedAtDesc(status: RequestStatus, requestType: RequestType): List<PlaceRequest>
    fun findAllByOrderByCreatedAtDesc(): List<PlaceRequest>
}

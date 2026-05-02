package com.taraethreads.tarae.request.repository

import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.domain.ShopRequest
import org.springframework.data.jpa.repository.JpaRepository

interface ShopRequestRepository : JpaRepository<ShopRequest, Long> {
    fun countByStatus(status: RequestStatus): Long
    fun findAllByStatusOrderByCreatedAtDesc(status: RequestStatus): List<ShopRequest>
    fun findAllByRequestTypeOrderByCreatedAtDesc(requestType: RequestType): List<ShopRequest>
    fun findAllByStatusAndRequestTypeOrderByCreatedAtDesc(
        status: RequestStatus,
        requestType: RequestType,
    ): List<ShopRequest>
    fun findAllByOrderByCreatedAtDesc(): List<ShopRequest>
}

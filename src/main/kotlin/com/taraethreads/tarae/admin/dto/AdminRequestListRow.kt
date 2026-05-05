package com.taraethreads.tarae.admin.dto

import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.domain.ShopRequest
import java.time.LocalDateTime

data class AdminRequestListRow(
    val id: Long,
    val name: String,
    val requestType: RequestType?,
    val status: RequestStatus,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun from(r: PlaceRequest) = AdminRequestListRow(
            id = r.id,
            name = r.name ?: "-",
            requestType = r.requestType,
            status = r.status,
            createdAt = runCatching { r.createdAt }.getOrNull(),
        )

        fun from(r: EventRequest) = AdminRequestListRow(
            id = r.id,
            name = r.title,
            requestType = null,
            status = r.status,
            createdAt = runCatching { r.createdAt }.getOrNull(),
        )

        fun from(r: ShopRequest) = AdminRequestListRow(
            id = r.id,
            name = r.name ?: "-",
            requestType = r.requestType,
            status = r.status,
            createdAt = runCatching { r.createdAt }.getOrNull(),
        )
    }
}

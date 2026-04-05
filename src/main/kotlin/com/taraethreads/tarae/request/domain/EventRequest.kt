package com.taraethreads.tarae.request.domain

import com.taraethreads.tarae.event.domain.EventType
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
import java.time.LocalDate

@Entity
@Table(name = "event_requests")
class EventRequest(
    @Column(nullable = false, length = 200)
    val title: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val eventType: EventType,

    @Column(nullable = false)
    val startDate: LocalDate,

    val endDate: LocalDate? = null,

    @Column(length = 255)
    val locationText: String? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var status: RequestStatus = RequestStatus.PENDING
        internal set

    fun approve() {
        if (status != RequestStatus.PENDING) throw CustomException(ErrorCode.REQUEST_ALREADY_PROCESSED)
        status = RequestStatus.APPROVED
    }

    fun reject() {
        if (status != RequestStatus.PENDING) throw CustomException(ErrorCode.REQUEST_ALREADY_PROCESSED)
        status = RequestStatus.REJECTED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventRequest) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

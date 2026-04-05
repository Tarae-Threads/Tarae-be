package com.taraethreads.tarae.request.domain

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
import java.math.BigDecimal

@Entity
@Table(name = "place_requests")
class PlaceRequest(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val requestType: RequestType,

    val placeId: Long? = null,

    @Column(length = 100)
    val name: String? = null,

    @Column(length = 255)
    val address: String? = null,

    @Column(length = 100)
    val addressDetail: String? = null,

    @Column(precision = 10, scale = 7)
    val lat: BigDecimal? = null,

    @Column(precision = 10, scale = 7)
    val lng: BigDecimal? = null,

    @Column(columnDefinition = "TEXT")
    val categoryIds: String? = null,

    @Column(length = 255)
    val hoursText: String? = null,

    @Column(length = 100)
    val closedDays: String? = null,

    @Column(length = 255)
    val brandsYarn: String? = null,

    @Column(length = 255)
    val brandsNeedle: String? = null,

    @Column(length = 255)
    val brandsNotions: String? = null,

    @Column(length = 255)
    val instagramUrl: String? = null,

    @Column(length = 255)
    val websiteUrl: String? = null,

    @Column(length = 255)
    val naverMapUrl: String? = null,

    @Column(length = 500)
    val tags: String? = null,

    @Column(columnDefinition = "TEXT")
    val note: String? = null,
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
        if (other !is PlaceRequest) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

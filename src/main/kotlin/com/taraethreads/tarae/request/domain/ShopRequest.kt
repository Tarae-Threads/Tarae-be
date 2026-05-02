package com.taraethreads.tarae.request.domain

import com.taraethreads.tarae.global.common.BaseEntity
import com.taraethreads.tarae.global.config.LongListJsonConverter
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "shop_requests")
class ShopRequest(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val requestType: RequestType,

    val shopId: Long? = null,

    @Column(length = 100)
    val name: String? = null,

    @Column(length = 255)
    val instagramUrl: String? = null,

    @Column(length = 255)
    val naverUrl: String? = null,

    @Column(length = 255)
    val websiteUrl: String? = null,

    @Convert(converter = LongListJsonConverter::class)
    @Column(columnDefinition = "JSON")
    val brandYarnIds: List<Long> = emptyList(),

    @Column(length = 255)
    val brandsYarn: String? = null,

    @Convert(converter = LongListJsonConverter::class)
    @Column(columnDefinition = "JSON")
    val brandNeedleIds: List<Long> = emptyList(),

    @Column(length = 255)
    val brandsNeedle: String? = null,

    @Convert(converter = LongListJsonConverter::class)
    @Column(columnDefinition = "JSON")
    val brandNotionsIds: List<Long> = emptyList(),

    @Column(length = 255)
    val brandsNotions: String? = null,

    @Convert(converter = LongListJsonConverter::class)
    @Column(columnDefinition = "JSON")
    val brandPatternbookIds: List<Long> = emptyList(),

    @Column(length = 255)
    val brandsPatternbook: String? = null,

    @Column(length = 500)
    val tags: String? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(length = 100)
    val email: String? = null,
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
        if (other !is ShopRequest) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

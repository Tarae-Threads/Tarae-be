package com.taraethreads.tarae.request.dto

import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PlaceRequestInput(
    @field:NotNull
    val requestType: RequestType?,
    val placeId: Long? = null,
    val name: String? = null,
    val address: String? = null,
    val addressDetail: String? = null,
    val lat: BigDecimal? = null,
    val lng: BigDecimal? = null,
    val categoryIds: List<Long>? = null,
    val categoryText: String? = null,
    val hoursText: String? = null,
    val closedDays: String? = null,
    val brandYarnIds: List<Long>? = null,
    val brandsYarn: String? = null,
    val brandNeedleIds: List<Long>? = null,
    val brandsNeedle: String? = null,
    val brandNotionsIds: List<Long>? = null,
    val brandsNotions: String? = null,
    val brandPatternbookIds: List<Long>? = null,
    val brandsPatternbook: String? = null,
    val instagramUrl: String? = null,
    val websiteUrl: String? = null,
    val naverMapUrl: String? = null,
    val tags: String? = null,
    val note: String? = null,
    @field:Email
    val email: String? = null,
) {
    fun toEntity() = PlaceRequest(
        requestType = requireNotNull(requestType) { "requestType은 필수입니다" },
        placeId = placeId,
        name = name,
        address = address,
        addressDetail = addressDetail,
        lat = lat,
        lng = lng,
        categoryIds = categoryIds ?: emptyList(),
        categoryText = categoryText,
        hoursText = hoursText,
        closedDays = closedDays,
        brandYarnIds = brandYarnIds ?: emptyList(),
        brandsYarn = brandsYarn,
        brandNeedleIds = brandNeedleIds ?: emptyList(),
        brandsNeedle = brandsNeedle,
        brandNotionsIds = brandNotionsIds ?: emptyList(),
        brandsNotions = brandsNotions,
        brandPatternbookIds = brandPatternbookIds ?: emptyList(),
        brandsPatternbook = brandsPatternbook,
        instagramUrl = instagramUrl,
        websiteUrl = websiteUrl,
        naverMapUrl = naverMapUrl,
        tags = tags,
        note = note,
        email = email,
    )
}

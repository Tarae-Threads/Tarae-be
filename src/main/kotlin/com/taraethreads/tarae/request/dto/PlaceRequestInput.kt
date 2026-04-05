package com.taraethreads.tarae.request.dto

import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestType
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
    val hoursText: String? = null,
    val closedDays: String? = null,
    val brandsYarn: String? = null,
    val brandsNeedle: String? = null,
    val brandsNotions: String? = null,
    val instagramUrl: String? = null,
    val websiteUrl: String? = null,
    val naverMapUrl: String? = null,
    val tags: String? = null,
    val note: String? = null,
) {
    fun toEntity() = PlaceRequest(
        requestType = requestType!!,
        placeId = placeId,
        name = name,
        address = address,
        addressDetail = addressDetail,
        lat = lat,
        lng = lng,
        categoryIds = categoryIds?.joinToString(","),
        hoursText = hoursText,
        closedDays = closedDays,
        brandsYarn = brandsYarn,
        brandsNeedle = brandsNeedle,
        brandsNotions = brandsNotions,
        instagramUrl = instagramUrl,
        websiteUrl = websiteUrl,
        naverMapUrl = naverMapUrl,
        tags = tags,
        note = note,
    )
}

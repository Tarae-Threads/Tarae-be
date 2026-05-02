package com.taraethreads.tarae.request.dto

import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.domain.ShopRequest
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull

data class ShopRequestInput(
    @field:NotNull
    val requestType: RequestType?,
    val shopId: Long? = null,
    val name: String? = null,
    val instagramUrl: String? = null,
    val naverUrl: String? = null,
    val websiteUrl: String? = null,
    val brandYarnIds: List<Long>? = null,
    val brandsYarn: String? = null,
    val brandNeedleIds: List<Long>? = null,
    val brandsNeedle: String? = null,
    val brandNotionsIds: List<Long>? = null,
    val brandsNotions: String? = null,
    val brandPatternbookIds: List<Long>? = null,
    val brandsPatternbook: String? = null,
    val tags: String? = null,
    val description: String? = null,
    @field:Email
    val email: String? = null,
) {
    fun toEntity() = ShopRequest(
        requestType = requireNotNull(requestType) { "requestType은 필수입니다" },
        shopId = shopId,
        name = name,
        instagramUrl = instagramUrl,
        naverUrl = naverUrl,
        websiteUrl = websiteUrl,
        brandYarnIds = brandYarnIds ?: emptyList(),
        brandsYarn = brandsYarn,
        brandNeedleIds = brandNeedleIds ?: emptyList(),
        brandsNeedle = brandsNeedle,
        brandNotionsIds = brandNotionsIds ?: emptyList(),
        brandsNotions = brandsNotions,
        brandPatternbookIds = brandPatternbookIds ?: emptyList(),
        brandsPatternbook = brandsPatternbook,
        tags = tags,
        description = description,
        email = email,
    )
}

package com.taraethreads.tarae.shop.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.shop.dto.ShopDetailResponse
import com.taraethreads.tarae.shop.dto.ShopListResponse
import com.taraethreads.tarae.shop.repository.ShopRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ShopService(
    private val shopRepository: ShopRepository,
) {

    fun getShops(tagId: Long?, keyword: String?): List<ShopListResponse> =
        shopRepository.findAllWithFilters(tagId, keyword)
            .map { ShopListResponse.from(it) }

    fun getShop(id: Long): ShopDetailResponse {
        val shop = shopRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.SHOP_NOT_FOUND) }
        if (!shop.active) throw CustomException(ErrorCode.SHOP_NOT_FOUND)
        return ShopDetailResponse.from(shop)
    }
}

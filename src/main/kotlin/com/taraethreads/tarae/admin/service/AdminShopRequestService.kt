package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.ShopCreateForm
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.domain.ShopRequest
import com.taraethreads.tarae.request.repository.ShopRequestRepository
import com.taraethreads.tarae.shop.domain.Shop
import com.taraethreads.tarae.shop.repository.ShopRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminShopRequestService(
    private val shopRequestRepository: ShopRequestRepository,
    private val shopRepository: ShopRepository,
    private val shopAssociationSyncer: ShopAssociationSyncer,
) {

    fun getShopRequests(status: RequestStatus?, requestType: RequestType?): List<ShopRequest> =
        when {
            status != null && requestType != null ->
                shopRequestRepository.findAllByStatusAndRequestTypeOrderByCreatedAtDesc(status, requestType)
            status != null -> shopRequestRepository.findAllByStatusOrderByCreatedAtDesc(status)
            requestType != null -> shopRequestRepository.findAllByRequestTypeOrderByCreatedAtDesc(requestType)
            else -> shopRequestRepository.findAllByOrderByCreatedAtDesc()
        }

    fun getShopRequest(id: Long): ShopRequest =
        shopRequestRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.SHOP_REQUEST_NOT_FOUND) }

    fun getShop(id: Long): Shop =
        shopRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.SHOP_NOT_FOUND) }

    @Transactional
    fun approveShopRequest(id: Long, form: ShopCreateForm) {
        val shopRequest = getShopRequest(id)
        shopRequest.approve()

        when (shopRequest.requestType) {
            RequestType.UPDATE -> {
                val shopId = shopRequest.shopId ?: throw CustomException(ErrorCode.INVALID_INPUT)
                val existing = shopRepository.findById(shopId)
                    .orElseThrow { CustomException(ErrorCode.SHOP_NOT_FOUND) }
                existing.update(form)
                shopAssociationSyncer.sync(existing, form)
            }
            RequestType.NEW -> {
                val newShop = Shop(
                    name = form.name,
                    instagramUrl = form.instagramUrl,
                    naverUrl = form.naverUrl,
                    websiteUrl = form.websiteUrl,
                    description = form.description,
                )
                shopRepository.save(newShop)
                shopAssociationSyncer.attach(newShop, form)
            }
        }
    }

    @Transactional
    fun rejectShopRequest(id: Long) {
        val shopRequest = getShopRequest(id)
        shopRequest.reject()
    }
}

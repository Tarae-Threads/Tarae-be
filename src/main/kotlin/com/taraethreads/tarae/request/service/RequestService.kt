package com.taraethreads.tarae.request.service

import com.taraethreads.tarae.request.dto.EventRequestInput
import com.taraethreads.tarae.request.dto.PlaceRequestInput
import com.taraethreads.tarae.request.dto.RequestResponse
import com.taraethreads.tarae.request.dto.ShopRequestInput
import com.taraethreads.tarae.request.repository.EventRequestRepository
import com.taraethreads.tarae.request.repository.PlaceRequestRepository
import com.taraethreads.tarae.request.repository.ShopRequestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RequestService(
    private val placeRequestRepository: PlaceRequestRepository,
    private val eventRequestRepository: EventRequestRepository,
    private val shopRequestRepository: ShopRequestRepository,
) {

    @Transactional
    fun requestPlace(input: PlaceRequestInput): RequestResponse {
        val saved = placeRequestRepository.save(input.toEntity())
        return RequestResponse(id = saved.id)
    }

    @Transactional
    fun requestEvent(input: EventRequestInput): RequestResponse {
        val saved = eventRequestRepository.save(input.toEntity())
        return RequestResponse(id = saved.id)
    }

    @Transactional
    fun requestShop(input: ShopRequestInput): RequestResponse {
        val saved = shopRequestRepository.save(input.toEntity())
        return RequestResponse(id = saved.id)
    }
}

package com.taraethreads.tarae.request.service

import com.taraethreads.tarae.request.dto.EventRequestInput
import com.taraethreads.tarae.request.dto.PlaceRequestInput
import com.taraethreads.tarae.request.dto.RequestResponse
import com.taraethreads.tarae.request.repository.EventRequestRepository
import com.taraethreads.tarae.request.repository.PlaceRequestRepository
import org.springframework.stereotype.Service

@Service
class RequestService(
    private val placeRequestRepository: PlaceRequestRepository,
    private val eventRequestRepository: EventRequestRepository,
) {

    fun requestPlace(input: PlaceRequestInput): RequestResponse {
        val saved = placeRequestRepository.save(input.toEntity())
        return RequestResponse(id = saved.id)
    }

    fun requestEvent(input: EventRequestInput): RequestResponse {
        val saved = eventRequestRepository.save(input.toEntity())
        return RequestResponse(id = saved.id)
    }
}

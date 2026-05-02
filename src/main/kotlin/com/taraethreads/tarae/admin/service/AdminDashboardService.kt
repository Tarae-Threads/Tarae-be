package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.inquiry.domain.InquiryStatus
import com.taraethreads.tarae.inquiry.repository.InquiryRepository
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.repository.EventRequestRepository
import com.taraethreads.tarae.request.repository.PlaceRequestRepository
import com.taraethreads.tarae.request.repository.ShopRequestRepository
import com.taraethreads.tarae.review.repository.ReviewRepository
import com.taraethreads.tarae.shop.repository.ShopRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class AdminDashboardService(
    private val placeRepository: PlaceRepository,
    private val eventRepository: EventRepository,
    private val placeRequestRepository: PlaceRequestRepository,
    private val eventRequestRepository: EventRequestRepository,
    private val shopRepository: ShopRepository,
    private val shopRequestRepository: ShopRequestRepository,
    private val reviewRepository: ReviewRepository,
    private val inquiryRepository: InquiryRepository,
) {

    fun getPlaceCount(): Long = placeRepository.count()

    fun getEventCount(): Long = eventRepository.count()

    fun getPendingPlaceRequestCount(): Long =
        placeRequestRepository.countByStatus(RequestStatus.PENDING)

    fun getPendingEventRequestCount(): Long =
        eventRequestRepository.countByStatus(RequestStatus.PENDING)

    fun getReviewCount(): Long = reviewRepository.count()

    fun getExpiringSoonEventCount(): Long {
        val today = LocalDate.now()
        return eventRepository.countExpiringSoon(today, today.plusDays(7))
    }

    fun getShopCount(): Long = shopRepository.count()

    fun getPendingShopRequestCount(): Long =
        shopRequestRepository.countByStatus(RequestStatus.PENDING)

    fun getPendingInquiryCount(): Long = inquiryRepository.countByStatus(InquiryStatus.PENDING)
}

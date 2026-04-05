package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.BrandRepository
import com.taraethreads.tarae.place.repository.CategoryRepository
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.place.repository.TagRepository
import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.repository.EventRequestRepository
import com.taraethreads.tarae.request.repository.PlaceRequestRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

class AdminRequestServiceTest {

    private val placeRequestRepository: PlaceRequestRepository = mockk()
    private val eventRequestRepository: EventRequestRepository = mockk()
    private val placeRepository: PlaceRepository = mockk()
    private val eventRepository: EventRepository = mockk()
    private val categoryRepository: CategoryRepository = mockk()
    private val tagRepository: TagRepository = mockk()
    private val brandRepository: BrandRepository = mockk()

    private val adminRequestService = AdminRequestService(
        placeRequestRepository = placeRequestRepository,
        eventRequestRepository = eventRequestRepository,
        placeRepository = placeRepository,
        eventRepository = eventRepository,
        categoryRepository = categoryRepository,
        tagRepository = tagRepository,
        brandRepository = brandRepository,
    )

    private fun placeRequest() = PlaceRequest(requestType = RequestType.NEW, name = "실과 바늘")
    private fun eventRequest() = EventRequest(
        title = "뜨개 팝업",
        eventType = EventType.EVENT_POPUP,
        startDate = LocalDate.of(2026, 5, 1),
    )

    @Nested
    inner class `장소 제보 목록 조회` {

        @Test
        fun `status가 null이면 전체 목록을 반환한다`() {
            // given
            every { placeRequestRepository.findAll() } returns listOf(placeRequest())

            // when
            val result = adminRequestService.getPlaceRequests(null)

            // then
            assertThat(result).hasSize(1)
        }

        @Test
        fun `status가 PENDING이면 PENDING 목록을 반환한다`() {
            // given
            every { placeRequestRepository.findAllByStatus(RequestStatus.PENDING) } returns listOf(placeRequest())

            // when
            val result = adminRequestService.getPlaceRequests(RequestStatus.PENDING)

            // then
            assertThat(result).hasSize(1)
        }
    }

    @Nested
    inner class `이벤트 제보 목록 조회` {

        @Test
        fun `status가 null이면 전체 목록을 반환한다`() {
            // given
            every { eventRequestRepository.findAll() } returns listOf(eventRequest())

            // when
            val result = adminRequestService.getEventRequests(null)

            // then
            assertThat(result).hasSize(1)
        }

        @Test
        fun `status가 APPROVED이면 APPROVED 목록을 반환한다`() {
            // given
            every { eventRequestRepository.findAllByStatus(RequestStatus.APPROVED) } returns emptyList()

            // when
            val result = adminRequestService.getEventRequests(RequestStatus.APPROVED)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `장소 제보 승인` {

        @Test
        fun `PENDING 제보를 승인하면 Place가 생성된다`() {
            // given
            val request = placeRequest()
            val savedPlace = Place(name = "실과 바늘", region = "서울", district = "성수", address = "서울 성동구")
            every { placeRequestRepository.findById(1L) } returns Optional.of(request)
            every { categoryRepository.findAllById(any()) } returns emptyList()
            every { tagRepository.findAllById(any()) } returns emptyList()
            every { brandRepository.findAllById(any()) } returns emptyList()
            every { placeRepository.save(any()) } returns savedPlace

            val form = PlaceCreateForm(
                name = "실과 바늘",
                region = "서울",
                district = "성수",
                address = "서울 성동구",
            )

            // when
            adminRequestService.approvePlaceRequest(1L, form)

            // then
            assertThat(request.status).isEqualTo(RequestStatus.APPROVED)
            verify { placeRepository.save(any()) }
        }

        @Test
        fun `이미 처리된 제보를 승인하면 예외가 발생한다`() {
            // given
            val request = placeRequest()
            request.approve()
            every { placeRequestRepository.findById(2L) } returns Optional.of(request)
            every { categoryRepository.findAllById(any()) } returns emptyList()
            every { tagRepository.findAllById(any()) } returns emptyList()
            every { brandRepository.findAllById(any()) } returns emptyList()
            every { placeRepository.save(any()) } returns mockk()

            // when & then
            assertThatThrownBy {
                adminRequestService.approvePlaceRequest(2L, PlaceCreateForm(
                    name = "실과 바늘", region = "서울", district = "성수", address = "서울 성동구"
                ))
            }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }
    }

    @Nested
    inner class `장소 제보 거절` {

        @Test
        fun `PENDING 제보를 거절하면 REJECTED 상태가 된다`() {
            // given
            val request = placeRequest()
            every { placeRequestRepository.findById(1L) } returns Optional.of(request)

            // when
            adminRequestService.rejectPlaceRequest(1L)

            // then
            assertThat(request.status).isEqualTo(RequestStatus.REJECTED)
        }

        @Test
        fun `이미 처리된 제보를 거절하면 예외가 발생한다`() {
            // given
            val request = placeRequest()
            request.reject()
            every { placeRequestRepository.findById(2L) } returns Optional.of(request)

            // when & then
            assertThatThrownBy { adminRequestService.rejectPlaceRequest(2L) }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }
    }

    @Nested
    inner class `이벤트 제보 승인` {

        @Test
        fun `PENDING 이벤트 제보를 승인하면 Event가 생성된다`() {
            // given
            val request = eventRequest()
            val savedEvent = Event(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
            )
            every { eventRequestRepository.findById(1L) } returns Optional.of(request)
            every { eventRepository.save(any()) } returns savedEvent

            val form = EventCreateForm(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
            )

            // when
            adminRequestService.approveEventRequest(1L, form)

            // then
            assertThat(request.status).isEqualTo(RequestStatus.APPROVED)
            verify { eventRepository.save(any()) }
        }

        @Test
        fun `이미 처리된 이벤트 제보를 승인하면 예외가 발생한다`() {
            // given
            val request = eventRequest()
            request.approve()
            every { eventRequestRepository.findById(2L) } returns Optional.of(request)

            // when & then
            assertThatThrownBy {
                adminRequestService.approveEventRequest(2L, EventCreateForm(
                    title = "뜨개 팝업",
                    eventType = EventType.EVENT_POPUP,
                    startDate = LocalDate.of(2026, 5, 1),
                ))
            }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }
    }

    @Nested
    inner class `이벤트 제보 거절` {

        @Test
        fun `PENDING 이벤트 제보를 거절하면 REJECTED 상태가 된다`() {
            // given
            val request = eventRequest()
            every { eventRequestRepository.findById(1L) } returns Optional.of(request)

            // when
            adminRequestService.rejectEventRequest(1L)

            // then
            assertThat(request.status).isEqualTo(RequestStatus.REJECTED)
        }

        @Test
        fun `이미 처리된 이벤트 제보를 거절하면 예외가 발생한다`() {
            // given
            val request = eventRequest()
            request.reject()
            every { eventRequestRepository.findById(2L) } returns Optional.of(request)

            // when & then
            assertThatThrownBy { adminRequestService.rejectEventRequest(2L) }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }
    }
}

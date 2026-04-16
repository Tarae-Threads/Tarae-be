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
import io.mockk.slot
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
    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val tagRepository: TagRepository = mockk(relaxed = true)
    private val brandRepository: BrandRepository = mockk(relaxed = true)
    private val placeAssociationSyncer = PlaceAssociationSyncer(categoryRepository, tagRepository, brandRepository)

    private val adminRequestService = AdminRequestService(
        placeRequestRepository = placeRequestRepository,
        eventRequestRepository = eventRequestRepository,
        placeRepository = placeRepository,
        eventRepository = eventRepository,
        placeAssociationSyncer = placeAssociationSyncer,
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
        fun `status와 requestType이 모두 null이면 전체 목록을 반환한다`() {
            // given
            every { placeRequestRepository.findAllByOrderByCreatedAtDesc() } returns listOf(placeRequest())

            // when
            val result = adminRequestService.getPlaceRequests(null, null)

            // then
            assertThat(result).hasSize(1)
        }

        @Test
        fun `status만 지정하면 해당 상태 목록을 반환한다`() {
            // given
            every { placeRequestRepository.findAllByStatusOrderByCreatedAtDesc(RequestStatus.PENDING) } returns listOf(placeRequest())

            // when
            val result = adminRequestService.getPlaceRequests(RequestStatus.PENDING, null)

            // then
            assertThat(result).hasSize(1)
        }

        @Test
        fun `requestType만 지정하면 해당 유형 목록을 반환한다`() {
            // given
            every { placeRequestRepository.findAllByRequestTypeOrderByCreatedAtDesc(RequestType.NEW) } returns listOf(placeRequest())

            // when
            val result = adminRequestService.getPlaceRequests(null, RequestType.NEW)

            // then
            assertThat(result).hasSize(1)
        }

        @Test
        fun `status와 requestType 모두 지정하면 교집합 목록을 반환한다`() {
            // given
            every { placeRequestRepository.findAllByStatusAndRequestTypeOrderByCreatedAtDesc(RequestStatus.PENDING, RequestType.UPDATE) } returns listOf(placeRequest())

            // when
            val result = adminRequestService.getPlaceRequests(RequestStatus.PENDING, RequestType.UPDATE)

            // then
            assertThat(result).hasSize(1)
        }
    }

    @Nested
    inner class `이벤트 제보 목록 조회` {

        @Test
        fun `status가 null이면 전체 목록을 반환한다`() {
            // given
            every { eventRequestRepository.findAllByOrderByCreatedAtDesc() } returns listOf(eventRequest())

            // when
            val result = adminRequestService.getEventRequests(null)

            // then
            assertThat(result).hasSize(1)
        }

        @Test
        fun `status가 APPROVED이면 APPROVED 목록을 반환한다`() {
            // given
            every { eventRequestRepository.findAllByStatusOrderByCreatedAtDesc(RequestStatus.APPROVED) } returns emptyList()

            // when
            val result = adminRequestService.getEventRequests(RequestStatus.APPROVED)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `장소 제보 승인` {

        @Test
        fun `NEW 제보를 승인하면 Place가 생성된다`() {
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
        fun `UPDATE 제보를 승인하면 기존 Place가 업데이트된다`() {
            // given
            val existingPlace = Place(name = "실과 바늘", region = "서울", district = "성수", address = "서울 성동구")
            val request = PlaceRequest(requestType = RequestType.UPDATE, placeId = 10L, name = "실과 바늘")
            every { placeRequestRepository.findById(1L) } returns Optional.of(request)
            every { placeRepository.findById(10L) } returns Optional.of(existingPlace)
            every { categoryRepository.findAllById(any()) } returns emptyList()
            every { tagRepository.findAllById(any()) } returns emptyList()
            every { brandRepository.findAllById(any()) } returns emptyList()

            val form = PlaceCreateForm(
                name = "실과 바늘 (수정)",
                region = "서울",
                district = "강남",
                address = "서울 강남구 역삼동",
            )

            // when
            adminRequestService.approvePlaceRequest(1L, form)

            // then
            assertThat(request.status).isEqualTo(RequestStatus.APPROVED)
            assertThat(existingPlace.name).isEqualTo("실과 바늘 (수정)")
            assertThat(existingPlace.district).isEqualTo("강남")
            assertThat(existingPlace.address).isEqualTo("서울 강남구 역삼동")
            verify(exactly = 0) { placeRepository.save(any()) }
        }

        @Test
        fun `UPDATE 제보인데 placeId에 해당하는 Place가 없으면 예외가 발생한다`() {
            // given
            val request = PlaceRequest(requestType = RequestType.UPDATE, placeId = 999L, name = "없는 장소")
            every { placeRequestRepository.findById(1L) } returns Optional.of(request)
            every { placeRepository.findById(999L) } returns Optional.empty()

            val form = PlaceCreateForm(
                name = "없는 장소", region = "서울", district = "성수", address = "서울 성동구"
            )

            // when & then
            assertThatThrownBy { adminRequestService.approvePlaceRequest(1L, form) }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PLACE_NOT_FOUND)
        }

        @Test
        fun `이미 처리된 제보를 승인하면 예외가 발생한다`() {
            // given
            val request = placeRequest()
            request.approve()
            every { placeRequestRepository.findById(2L) } returns Optional.of(request)

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
        fun `placeId가 있으면 Place를 연결한다`() {
            // given
            val request = eventRequest()
            val place = Place(name = "실과 바늘", region = "서울", district = "성수", address = "서울 성동구")
            every { eventRequestRepository.findById(3L) } returns Optional.of(request)
            every { placeRepository.findById(10L) } returns Optional.of(place)
            val eventSlot = slot<Event>()
            every { eventRepository.save(capture(eventSlot)) } answers { eventSlot.captured }

            val form = EventCreateForm(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
                placeId = 10L,
            )

            // when
            adminRequestService.approveEventRequest(3L, form)

            // then
            assertThat(eventSlot.captured.place).isEqualTo(place)
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

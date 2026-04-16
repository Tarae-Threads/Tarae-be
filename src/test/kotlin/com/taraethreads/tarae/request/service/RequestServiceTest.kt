package com.taraethreads.tarae.request.service

import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.dto.EventRequestInput
import com.taraethreads.tarae.request.dto.PlaceRequestInput
import com.taraethreads.tarae.request.repository.EventRequestRepository
import com.taraethreads.tarae.request.repository.PlaceRequestRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class RequestServiceTest {

    private val placeRequestRepository: PlaceRequestRepository = mockk()
    private val eventRequestRepository: EventRequestRepository = mockk()
    private val requestService = RequestService(placeRequestRepository, eventRequestRepository)

    @Nested
    inner class `장소 등록 요청` {

        @Test
        fun `장소 요청을 저장하고 id를 반환한다`() {
            // given
            val input = PlaceRequestInput(requestType = RequestType.NEW, name = "실과 바늘", address = "서울 성동구")
            val slot = slot<PlaceRequest>()
            val saved = PlaceRequest(requestType = RequestType.NEW, name = "실과 바늘")
            every { placeRequestRepository.save(capture(slot)) } returns saved

            // when
            requestService.requestPlace(input)

            // then
            verify { placeRequestRepository.save(any()) }
            assertThat(slot.captured.requestType).isEqualTo(RequestType.NEW)
            assertThat(slot.captured.name).isEqualTo("실과 바늘")
        }
    }

    @Nested
    inner class `이벤트 등록 요청` {

        @Test
        fun `이벤트 요청을 저장하고 id를 반환한다`() {
            // given
            val input = EventRequestInput(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
            )
            val slot = slot<EventRequest>()
            val saved = EventRequest(title = "뜨개 팝업", eventType = EventType.EVENT_POPUP, startDate = LocalDate.of(2026, 5, 1))
            every { eventRequestRepository.save(capture(slot)) } returns saved

            // when
            requestService.requestEvent(input)

            // then
            verify { eventRequestRepository.save(any()) }
            assertThat(slot.captured.title).isEqualTo("뜨개 팝업")
            assertThat(slot.captured.eventType).isEqualTo(EventType.EVENT_POPUP)
        }

        @Test
        fun `위치 및 링크 필드가 엔티티에 매핑된다`() {
            // given
            val input = EventRequestInput(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
                lat = BigDecimal("37.5665350"),
                lng = BigDecimal("126.9780150"),
                instagramUrl = "https://instagram.com/test",
                websiteUrl = "https://example.com",
                naverMapUrl = "https://naver.me/test",
            )
            val slot = slot<EventRequest>()
            val saved = EventRequest(title = "뜨개 팝업", eventType = EventType.EVENT_POPUP, startDate = LocalDate.of(2026, 5, 1))
            every { eventRequestRepository.save(capture(slot)) } returns saved

            // when
            requestService.requestEvent(input)

            // then
            with(slot.captured) {
                assertThat(lat).isEqualByComparingTo(BigDecimal("37.5665350"))
                assertThat(lng).isEqualByComparingTo(BigDecimal("126.9780150"))
                assertThat(instagramUrl).isEqualTo("https://instagram.com/test")
                assertThat(websiteUrl).isEqualTo("https://example.com")
                assertThat(naverMapUrl).isEqualTo("https://naver.me/test")
            }
        }
    }
}

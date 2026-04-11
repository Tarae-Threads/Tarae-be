package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.service.EventService
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.dto.PlaceDetailResponse
import com.taraethreads.tarae.place.dto.PlaceListResponse
import com.taraethreads.tarae.place.repository.PlaceRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

class PlaceServiceTest {

    private val placeRepository: PlaceRepository = mockk()
    private val eventService: EventService = mockk()
    private val placeService: PlaceService = PlaceService(placeRepository, eventService)

    private fun place(name: String = "장소A", region: String = "서울") =
        Place(name = name, region = region, district = "성수", address = "서울 성동구")

    @Nested
    inner class `목록 조회` {

        @Test
        fun `필터 파라미터를 repository에 그대로 전달하고 PlaceListResponse 목록을 반환한다`() {
            // given
            every { placeRepository.findAllWithFilters("서울", 1L, null, null) } returns listOf(place())

            // when
            val result: List<PlaceListResponse> = placeService.getPlaces(region = "서울", categoryId = 1L, tagId = null, keyword = null)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("장소A")
            assertThat(result[0].status).isEqualTo("OPEN")
            verify { placeRepository.findAllWithFilters("서울", 1L, null, null) }
        }

        @Test
        fun `keyword를 포함한 파라미터를 repository에 그대로 전달한다`() {
            // given
            every { placeRepository.findAllWithFilters(null, null, null, "실과") } returns listOf(place(name = "실과바늘"))

            // when
            val result: List<PlaceListResponse> = placeService.getPlaces(region = null, categoryId = null, tagId = null, keyword = "실과")

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("실과바늘")
            verify { placeRepository.findAllWithFilters(null, null, null, "실과") }
        }

        @Test
        fun `필터 없이 전체 조회한다`() {
            // given
            every { placeRepository.findAllWithFilters(null, null, null, null) } returns emptyList()

            // when
            val result: List<PlaceListResponse> = placeService.getPlaces(null, null, null, null)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `상세 조회` {

        @Test
        fun `존재하는 id로 조회하면 PlaceDetailResponse를 반환한다`() {
            // given
            every { placeRepository.findById(1L) } returns Optional.of(place())
            every { eventService.findActiveEventsByPlaceId(1L) } returns emptyList()

            // when
            val result: PlaceDetailResponse = placeService.getPlace(1L)

            // then
            assertThat(result.name).isEqualTo("장소A")
            assertThat(result.region).isEqualTo("서울")
            assertThat(result.status).isEqualTo("OPEN")
            verify { eventService.findActiveEventsByPlaceId(1L) }
        }

        @Test
        fun `활성 이벤트가 있는 장소 조회 시 events 리스트에 포함된다`() {
            // given
            val event = Event(
                title = "봄 클래스 모집",
                eventType = EventType.TESTER_RECRUIT,
                startDate = LocalDate.of(2026, 4, 15),
                endDate = LocalDate.of(2026, 5, 10),
                active = true,
            )
            every { placeRepository.findById(1L) } returns Optional.of(place())
            every { eventService.findActiveEventsByPlaceId(1L) } returns listOf(event)

            // when
            val result: PlaceDetailResponse = placeService.getPlace(1L)

            // then
            assertThat(result.events).hasSize(1)
            assertThat(result.events[0].title).isEqualTo("봄 클래스 모집")
            assertThat(result.events[0].eventType).isEqualTo("TESTER_RECRUIT")
            assertThat(result.events[0].active).isTrue()
        }

        @Test
        fun `이벤트가 없는 장소 조회 시 events는 빈 배열이다`() {
            // given
            every { placeRepository.findById(1L) } returns Optional.of(place())
            every { eventService.findActiveEventsByPlaceId(1L) } returns emptyList()

            // when
            val result: PlaceDetailResponse = placeService.getPlace(1L)

            // then
            assertThat(result.events).isEmpty()
        }

        @Test
        fun `존재하지 않는 id로 조회하면 PLACE_NOT_FOUND 예외가 발생한다`() {
            // given
            every { placeRepository.findById(999L) } returns Optional.empty()

            // when & then
            assertThatThrownBy { placeService.getPlace(999L) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.PLACE_NOT_FOUND }
        }
    }
}

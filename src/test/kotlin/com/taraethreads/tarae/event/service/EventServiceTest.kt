package com.taraethreads.tarae.event.service

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

class EventServiceTest {

    private val eventRepository: EventRepository = mockk()
    private val eventService: EventService = EventService(eventRepository)

    private fun event(
        title: String = "테스트 이벤트",
        eventType: EventType = EventType.SALE,
        active: Boolean = true,
    ) = Event(title = title, eventType = eventType, startDate = LocalDate.of(2026, 5, 1), active = active)

    @Nested
    inner class `목록 조회` {

        @Test
        fun `필터 파라미터를 repository에 그대로 전달한다`() {
            // given
            every { eventRepository.findAllWithFilters(EventType.SALE, true) } returns listOf(event())

            // when
            val result = eventService.getEvents(EventType.SALE, true)

            // then
            assertThat(result).hasSize(1)
            verify { eventRepository.findAllWithFilters(EventType.SALE, true) }
        }

        @Test
        fun `필터 없이 전체 조회한다`() {
            // given
            every { eventRepository.findAllWithFilters(null, null) } returns emptyList()

            // when
            val result = eventService.getEvents(null, null)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `상세 조회` {

        @Test
        fun `존재하는 id로 조회하면 Event를 반환한다`() {
            // given
            every { eventRepository.findById(1L) } returns Optional.of(event())

            // when
            val result = eventService.getEvent(1L)

            // then
            assertThat(result.title).isEqualTo("테스트 이벤트")
        }

        @Test
        fun `존재하지 않는 id로 조회하면 EVENT_NOT_FOUND 예외가 발생한다`() {
            // given
            every { eventRepository.findById(999L) } returns Optional.empty()

            // when & then
            assertThatThrownBy { eventService.getEvent(999L) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.EVENT_NOT_FOUND }
        }
    }
}

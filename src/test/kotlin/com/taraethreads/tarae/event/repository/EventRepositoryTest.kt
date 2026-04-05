package com.taraethreads.tarae.event.repository

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.global.config.JpaConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig::class)
class EventRepositoryTest {

    @Autowired
    lateinit var eventRepository: EventRepository

    private fun createEvent(
        title: String = "테스트 이벤트",
        eventType: EventType = EventType.SALE,
        active: Boolean = true,
        endDate: LocalDate? = null,
    ): Event = eventRepository.save(
        Event(
            title = title,
            eventType = eventType,
            startDate = LocalDate.now().minusDays(1),
            endDate = endDate,
            active = active,
        )
    )

    @Nested
    inner class `필터 없이 전체 조회` {
        @Test
        fun `모든 이벤트가 반환된다`() {
            // given
            createEvent(title = "이벤트A", eventType = EventType.SALE)
            createEvent(title = "이벤트B", eventType = EventType.EVENT_POPUP)

            // when
            val result = eventRepository.findAllWithFilters(null, null)

            // then
            assertThat(result).hasSize(2)
        }
    }

    @Nested
    inner class `eventType 필터` {
        @Test
        fun `eventType으로 필터링하면 해당 타입 이벤트만 반환된다`() {
            // given
            createEvent(title = "세일 이벤트", eventType = EventType.SALE)
            createEvent(title = "팝업 이벤트", eventType = EventType.EVENT_POPUP)

            // when
            val result = eventRepository.findAllWithFilters(EventType.SALE, null)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("세일 이벤트")
        }
    }

    @Nested
    inner class `active 필터` {
        @Test
        fun `active=true 필터링하면 활성 이벤트만 반환된다`() {
            // given
            createEvent(title = "활성 이벤트", active = true)
            createEvent(title = "비활성 이벤트", active = false)

            // when
            val result = eventRepository.findAllWithFilters(null, true)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("활성 이벤트")
        }
    }

    @Nested
    inner class `복합 필터` {
        @Test
        fun `eventType과 active를 함께 사용하면 모두 만족하는 이벤트만 반환된다`() {
            // given
            createEvent(title = "활성 세일", eventType = EventType.SALE, active = true)
            createEvent(title = "비활성 세일", eventType = EventType.SALE, active = false)
            createEvent(title = "활성 팝업", eventType = EventType.EVENT_POPUP, active = true)

            // when
            val result = eventRepository.findAllWithFilters(EventType.SALE, true)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("활성 세일")
        }
    }

    @Nested
    inner class `만료일 필터` {
        @Test
        fun `만료일이 오늘보다 이전인 이벤트는 조회되지 않는다`() {
            // given
            createEvent(title = "만료된 이벤트", endDate = LocalDate.now().minusDays(1))
            createEvent(title = "진행 중인 이벤트", endDate = LocalDate.now().plusDays(7))

            // when
            val result = eventRepository.findAllWithFilters(null, null)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("진행 중인 이벤트")
        }

        @Test
        fun `만료일이 오늘인 이벤트는 조회된다`() {
            // given
            createEvent(title = "오늘 마감 이벤트", endDate = LocalDate.now())

            // when
            val result = eventRepository.findAllWithFilters(null, null)

            // then
            assertThat(result).hasSize(1)
        }

        @Test
        fun `만료일이 없는 이벤트는 항상 조회된다`() {
            // given
            createEvent(title = "상시 이벤트", endDate = null)

            // when
            val result = eventRepository.findAllWithFilters(null, null)

            // then
            assertThat(result).hasSize(1)
        }
    }

    @Nested
    inner class `만료 이벤트 비활성화` {
        @Test
        fun `만료일이 지난 활성 이벤트를 비활성화하고 처리 건수를 반환한다`() {
            // given
            createEvent(title = "만료된 이벤트", active = true, endDate = LocalDate.now().minusDays(1))
            createEvent(title = "진행 중인 이벤트", active = true, endDate = LocalDate.now().plusDays(7))

            // when
            val count = eventRepository.deactivateExpiredEvents(LocalDate.now())

            // then
            assertThat(count).isEqualTo(1)
        }

        @Test
        fun `이미 비활성화된 만료 이벤트는 카운트에 포함되지 않는다`() {
            // given
            createEvent(title = "이미 비활성화됨", active = false, endDate = LocalDate.now().minusDays(1))

            // when
            val count = eventRepository.deactivateExpiredEvents(LocalDate.now())

            // then
            assertThat(count).isEqualTo(0)
        }
    }
}

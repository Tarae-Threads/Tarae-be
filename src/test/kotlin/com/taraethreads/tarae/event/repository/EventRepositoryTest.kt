package com.taraethreads.tarae.event.repository

import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.global.config.JpaConfig
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.PlaceRepository
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

    @Autowired
    lateinit var placeRepository: PlaceRepository

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
    inner class `findPublicEventsByPlaceId` {

        private fun savedPlace() = placeRepository.save(
            Place(name = "테스트 공방", region = "서울", district = "성수", address = "서울 성동구 테스트로 1")
        )

        @Test
        fun `active=true 이고 만료되지 않은 이벤트만 반환한다`() {
            // given
            val place = savedPlace()
            eventRepository.save(Event(title = "활성 진행", eventType = EventType.SALE, startDate = LocalDate.now(), place = place, active = true))
            eventRepository.save(Event(title = "비활성", eventType = EventType.SALE, startDate = LocalDate.now(), place = place, active = false))
            eventRepository.save(Event(title = "만료됨", eventType = EventType.SALE, startDate = LocalDate.now().minusDays(10), endDate = LocalDate.now().minusDays(1), place = place, active = true))

            // when
            val result = eventRepository.findPublicEventsByPlaceId(place.id, LocalDate.now())

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("활성 진행")
        }

        @Test
        fun `다른 장소의 이벤트는 반환하지 않는다`() {
            val place1 = savedPlace()
            val place2 = savedPlace()
            eventRepository.save(Event(title = "장소1 이벤트", eventType = EventType.SALE, startDate = LocalDate.now(), place = place1))
            eventRepository.save(Event(title = "장소2 이벤트", eventType = EventType.SALE, startDate = LocalDate.now(), place = place2))

            val result = eventRepository.findPublicEventsByPlaceId(place1.id, LocalDate.now())

            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("장소1 이벤트")
        }
    }

    @Nested
    inner class `findAllForAdmin` {
        @Test
        fun `ALL 필터는 만료 + 비활성 이벤트도 모두 반환한다`() {
            createEvent(title = "활성", active = true)
            createEvent(title = "비활성", active = false)
            createEvent(title = "만료", endDate = LocalDate.now().minusDays(1))

            val result = eventRepository.findAllForAdmin(
                com.taraethreads.tarae.admin.dto.AdminEventStatusFilter.ALL,
                org.springframework.data.domain.PageRequest.of(0, 10),
            )

            assertThat(result.totalElements).isEqualTo(3)
        }

        @Test
        fun `EXPIRED 필터는 종료일이 지난 이벤트만 반환한다`() {
            createEvent(title = "만료", endDate = LocalDate.now().minusDays(1))
            createEvent(title = "진행중", endDate = LocalDate.now().plusDays(3))
            createEvent(title = "상시", endDate = null)

            val result = eventRepository.findAllForAdmin(
                com.taraethreads.tarae.admin.dto.AdminEventStatusFilter.EXPIRED,
                org.springframework.data.domain.PageRequest.of(0, 10),
            )

            assertThat(result.totalElements).isEqualTo(1)
            assertThat(result.content[0].title).isEqualTo("만료")
        }

        @Test
        fun `EXPIRING_SOON 필터는 D-7 이내 이벤트만 반환한다`() {
            createEvent(title = "임박", endDate = LocalDate.now().plusDays(3))
            createEvent(title = "여유", endDate = LocalDate.now().plusDays(30))
            createEvent(title = "만료", endDate = LocalDate.now().minusDays(1))

            val result = eventRepository.findAllForAdmin(
                com.taraethreads.tarae.admin.dto.AdminEventStatusFilter.EXPIRING_SOON,
                org.springframework.data.domain.PageRequest.of(0, 10),
            )

            assertThat(result.totalElements).isEqualTo(1)
            assertThat(result.content[0].title).isEqualTo("임박")
        }
    }

    @Nested
    inner class `countExpiringSoon` {
        @Test
        fun `D-7 이내 활성 이벤트 수를 반환한다`() {
            createEvent(title = "임박1", active = true, endDate = LocalDate.now().plusDays(3))
            createEvent(title = "임박2", active = true, endDate = LocalDate.now().plusDays(7))
            createEvent(title = "여유", active = true, endDate = LocalDate.now().plusDays(30))
            createEvent(title = "임박이지만 비활성", active = false, endDate = LocalDate.now().plusDays(3))

            val count = eventRepository.countExpiringSoon(LocalDate.now(), LocalDate.now().plusDays(7))

            assertThat(count).isEqualTo(2)
        }
    }
}

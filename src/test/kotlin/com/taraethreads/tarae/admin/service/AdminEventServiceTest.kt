package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.EventCreateForm
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.repository.EventRepository
import com.taraethreads.tarae.place.repository.PlaceRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

class AdminEventServiceTest {

    private val eventRepository: EventRepository = mockk()
    private val placeRepository: PlaceRepository = mockk()
    private val service = AdminEventService(eventRepository, placeRepository)

    private fun event() = Event(
        title = "이벤트",
        eventType = EventType.SALE,
        startDate = LocalDate.now(),
    )

    @Test
    fun `createBulk 는 폼들을 모두 저장한다`() {
        val saved = slot<Event>()
        every { eventRepository.save(capture(saved)) } answers { saved.captured }

        val ids = service.createBulk(
            listOf(
                EventCreateForm(title = "A", eventType = EventType.SALE, startDate = LocalDate.now()),
                EventCreateForm(title = "B", eventType = EventType.EVENT_POPUP, startDate = LocalDate.now()),
            )
        )

        assertThat(ids).hasSize(2)
        verify(exactly = 2) { eventRepository.save(any()) }
    }

    @Test
    fun `createBulk 는 URL 필드를 매핑한다`() {
        val saved = slot<Event>()
        every { eventRepository.save(capture(saved)) } answers { saved.captured }

        service.createBulk(
            listOf(
                EventCreateForm(
                    title = "URL 테스트",
                    eventType = EventType.SALE,
                    startDate = LocalDate.now(),
                    instagramUrl = "https://instagram.com/test",
                    websiteUrl = "https://example.com",
                    naverMapUrl = "https://naver.me/test",
                ),
            )
        )

        assertThat(saved.captured.instagramUrl).isEqualTo("https://instagram.com/test")
        assertThat(saved.captured.websiteUrl).isEqualTo("https://example.com")
        assertThat(saved.captured.naverMapUrl).isEqualTo("https://naver.me/test")
    }

    @Test
    fun `toggleActive 는 active 상태를 뒤집는다`() {
        val event = event()
        every { eventRepository.findById(1L) } returns Optional.of(event)

        service.toggleActive(1L)

        assertThat(event.active).isFalse()
    }

    @Test
    fun `update 는 도메인 메서드를 호출한다`() {
        val event = event()
        every { eventRepository.findById(1L) } returns Optional.of(event)

        service.update(1L, EventCreateForm(title = "수정", eventType = EventType.SALE, startDate = LocalDate.now()))

        assertThat(event.title).isEqualTo("수정")
    }

    @Test
    fun `delete 는 이벤트를 삭제한다`() {
        val event = event()
        every { eventRepository.findById(1L) } returns Optional.of(event)
        every { eventRepository.delete(event) } returns Unit

        service.delete(1L)

        verify { eventRepository.delete(event) }
    }
}

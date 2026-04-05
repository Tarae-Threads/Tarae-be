package com.taraethreads.tarae.event.scheduler

import com.taraethreads.tarae.event.repository.EventRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EventSchedulerTest {

    private val eventRepository: EventRepository = mockk()
    private val eventScheduler = EventScheduler(eventRepository)

    @Test
    fun `오늘 날짜 기준으로 만료된 이벤트를 비활성화한다`() {
        // given
        every { eventRepository.deactivateExpiredEvents(any()) } returns 3

        // when
        eventScheduler.deactivateExpiredEvents()

        // then
        verify { eventRepository.deactivateExpiredEvents(LocalDate.now()) }
    }
}

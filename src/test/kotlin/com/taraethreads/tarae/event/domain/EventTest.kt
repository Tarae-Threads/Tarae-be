package com.taraethreads.tarae.event.domain

import com.taraethreads.tarae.admin.dto.EventCreateForm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class EventTest {

    private fun event() = Event(
        title = "기존 이벤트",
        eventType = EventType.SALE,
        startDate = LocalDate.of(2026, 1, 1),
        endDate = LocalDate.of(2026, 1, 31),
    )

    @Test
    fun `update 는 폼 데이터를 적용한다`() {
        val event = event()
        val form = EventCreateForm(
            title = "수정된 이벤트",
            eventType = EventType.EVENT_POPUP,
            startDate = LocalDate.of(2026, 5, 1),
            endDate = LocalDate.of(2026, 5, 7),
            locationText = "서울 성수",
            description = "업데이트",
        )

        event.update(form)

        assertThat(event.title).isEqualTo("수정된 이벤트")
        assertThat(event.eventType).isEqualTo(EventType.EVENT_POPUP)
        assertThat(event.startDate).isEqualTo(LocalDate.of(2026, 5, 1))
        assertThat(event.endDate).isEqualTo(LocalDate.of(2026, 5, 7))
        assertThat(event.locationText).isEqualTo("서울 성수")
        assertThat(event.description).isEqualTo("업데이트")
    }

    @Test
    fun `deactivate 는 active 를 false 로 만든다`() {
        val event = event()
        event.deactivate()
        assertThat(event.active).isFalse()
    }

    @Test
    fun `activate 는 active 를 true 로 만든다`() {
        val event = event()
        event.deactivate()
        event.activate()
        assertThat(event.active).isTrue()
    }
}

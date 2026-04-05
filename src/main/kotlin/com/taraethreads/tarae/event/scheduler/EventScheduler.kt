package com.taraethreads.tarae.event.scheduler

import com.taraethreads.tarae.event.repository.EventRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class EventScheduler(
    private val eventRepository: EventRepository,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    fun deactivateExpiredEvents() {
        val count = eventRepository.deactivateExpiredEvents(LocalDate.now())
        log.info("만료된 이벤트 비활성화 완료: {}건", count)
    }
}

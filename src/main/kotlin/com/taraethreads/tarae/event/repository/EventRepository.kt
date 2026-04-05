package com.taraethreads.tarae.event.repository

import com.taraethreads.tarae.event.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface EventRepository : JpaRepository<Event, Long>, EventRepositoryCustom {

    @Modifying
    @Query("UPDATE Event e SET e.active = false WHERE e.endDate IS NOT NULL AND e.endDate < :today AND e.active = true")
    fun deactivateExpiredEvents(@Param("today") today: LocalDate): Int
}

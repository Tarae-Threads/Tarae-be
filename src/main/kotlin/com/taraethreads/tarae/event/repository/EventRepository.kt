package com.taraethreads.tarae.event.repository

import com.taraethreads.tarae.event.domain.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface EventRepository : JpaRepository<Event, Long>, EventRepositoryCustom {

    @Query(
        "SELECT e FROM Event e WHERE e.place.id = :placeId AND e.active = true " +
            "AND (e.endDate IS NULL OR e.endDate >= :today)"
    )
    fun findPublicEventsByPlaceId(
        @Param("placeId") placeId: Long,
        @Param("today") today: LocalDate,
    ): List<Event>
}

package com.taraethreads.tarae.event.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.domain.QEvent.event
import java.time.LocalDate

class EventRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : EventRepositoryCustom {

    override fun findAllWithFilters(eventType: EventType?, active: Boolean?): List<Event> {
        val query = queryFactory
            .selectFrom(event)
            .where(event.endDate.isNull.or(event.endDate.goe(LocalDate.now())))

        if (eventType != null) {
            query.where(event.eventType.eq(eventType))
        }

        if (active != null) {
            query.where(event.active.eq(active))
        }

        return query.fetch()
    }
}

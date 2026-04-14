package com.taraethreads.tarae.event.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taraethreads.tarae.admin.dto.AdminEventStatusFilter
import com.taraethreads.tarae.event.domain.Event
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.event.domain.QEvent.event
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate

class EventRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : EventRepositoryCustom {

    override fun findAllWithFilters(eventType: EventType?, active: Boolean?): List<Event> {
        val today = LocalDate.now()
        val query = queryFactory
            .selectFrom(event)
            .where(
                event.active.isTrue,
                event.endDate.isNull.or(event.endDate.goe(today)),
            )

        if (eventType != null) {
            query.where(event.eventType.eq(eventType))
        }

        // active 파라미터는 의미상 더 이상 사용하지 않지만 시그니처는 유지 (공개 API 호환).
        // active=false 가 명시적으로 들어오면 빈 결과 반환.
        if (active == false) {
            return emptyList()
        }

        return query.fetch()
    }

    override fun findAllForAdmin(filter: AdminEventStatusFilter, pageable: Pageable): Page<Event> {
        val today = LocalDate.now()
        val threshold = today.plusDays(7)

        val query = queryFactory
            .selectFrom(event)

        when (filter) {
            AdminEventStatusFilter.ALL -> {}
            AdminEventStatusFilter.ONGOING ->
                query.where(event.endDate.isNull.or(event.endDate.goe(today)))
            AdminEventStatusFilter.EXPIRING_SOON ->
                query.where(event.endDate.isNotNull, event.endDate.goe(today), event.endDate.loe(threshold))
            AdminEventStatusFilter.EXPIRED ->
                query.where(event.endDate.isNotNull, event.endDate.lt(today))
        }

        val countQuery = queryFactory.selectFrom(event)
        when (filter) {
            AdminEventStatusFilter.ALL -> {}
            AdminEventStatusFilter.ONGOING ->
                countQuery.where(event.endDate.isNull.or(event.endDate.goe(today)))
            AdminEventStatusFilter.EXPIRING_SOON ->
                countQuery.where(event.endDate.isNotNull, event.endDate.goe(today), event.endDate.loe(threshold))
            AdminEventStatusFilter.EXPIRED ->
                countQuery.where(event.endDate.isNotNull, event.endDate.lt(today))
        }
        val totalCount = countQuery.fetch().size.toLong()

        val content = query
            .orderBy(event.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(content, pageable, totalCount)
    }

    override fun countExpiringSoon(today: LocalDate, threshold: LocalDate): Long {
        return queryFactory
            .selectFrom(event)
            .where(
                event.active.isTrue,
                event.endDate.isNotNull,
                event.endDate.goe(today),
                event.endDate.loe(threshold),
            )
            .fetch()
            .size
            .toLong()
    }
}

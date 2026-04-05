package com.taraethreads.tarae.place.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.domain.QCategory.category
import com.taraethreads.tarae.place.domain.QPlace.place
import com.taraethreads.tarae.place.domain.QTag.tag

class PlaceRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PlaceRepositoryCustom {

    override fun findAllWithFilters(region: String?, categoryId: Long?, tagId: Long?): List<Place> {
        val query = queryFactory
            .selectDistinct(place)
            .from(place)

        if (region != null) {
            query.where(place.region.eq(region))
        }

        if (categoryId != null) {
            query.join(place.categories, category)
                .where(category.id.eq(categoryId))
        }

        if (tagId != null) {
            query.join(place.tags, tag)
                .where(tag.id.eq(tagId))
        }

        return query.fetch()
    }
}

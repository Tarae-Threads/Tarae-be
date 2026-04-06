package com.taraethreads.tarae.place.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.domain.QBrand
import com.taraethreads.tarae.place.domain.QCategory.category
import com.taraethreads.tarae.place.domain.QPlace.place
import com.taraethreads.tarae.place.domain.QTag
import com.taraethreads.tarae.place.domain.QTag.tag

class PlaceRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PlaceRepositoryCustom {

    override fun findAllWithFilters(region: String?, categoryId: Long?, tagId: Long?, keyword: String?): List<Place> {
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

        val tokens = keyword?.trim()?.split("\\s+".toRegex())?.filter { it.isNotEmpty() } ?: emptyList()
        if (tokens.isNotEmpty()) {
            val searchTag = QTag("searchTag")
            val searchBrand = QBrand("searchBrand")
            query.leftJoin(place.tags, searchTag)
            query.leftJoin(place.brands, searchBrand)

            tokens.forEach { token ->
                query.where(tokenCondition(token, searchTag, searchBrand))
            }
        }

        return query.fetch()
    }

    private fun tokenCondition(token: String, searchTag: QTag, searchBrand: QBrand): BooleanExpression =
        place.name.containsIgnoreCase(token)
            .or(place.district.containsIgnoreCase(token))
            .or(place.address.containsIgnoreCase(token))
            .or(place.description.containsIgnoreCase(token))
            .or(searchTag.name.containsIgnoreCase(token))
            .or(searchBrand.name.containsIgnoreCase(token))
}

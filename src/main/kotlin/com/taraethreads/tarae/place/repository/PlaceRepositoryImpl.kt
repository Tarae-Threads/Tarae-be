package com.taraethreads.tarae.place.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.domain.QBrand
import com.taraethreads.tarae.place.domain.QCategory.category
import com.taraethreads.tarae.place.domain.QPlace.place
import com.taraethreads.tarae.place.domain.QPlaceBrand
import com.taraethreads.tarae.place.domain.QPlaceCategory
import com.taraethreads.tarae.place.domain.QPlaceTag
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
            val placeCategory = QPlaceCategory.placeCategory
            query.join(place.placeCategories, placeCategory)
                .join(placeCategory.category, category)
                .where(category.id.eq(categoryId))
        }

        if (tagId != null) {
            val placeTag = QPlaceTag.placeTag
            query.join(place.placeTags, placeTag)
                .join(placeTag.tag, tag)
                .where(tag.id.eq(tagId))
        }

        val tokens = keyword?.trim()?.split("\\s+".toRegex())?.filter { it.isNotEmpty() } ?: emptyList()
        if (tokens.isNotEmpty()) {
            val searchPlaceTag = QPlaceTag("searchPlaceTag")
            val searchTag = QTag("searchTag")
            val searchPlaceBrand = QPlaceBrand("searchPlaceBrand")
            val searchBrand = QBrand("searchBrand")
            query.leftJoin(place.placeTags, searchPlaceTag)
                .leftJoin(searchPlaceTag.tag, searchTag)
            query.leftJoin(place.placeBrands, searchPlaceBrand)
                .leftJoin(searchPlaceBrand.brand, searchBrand)

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

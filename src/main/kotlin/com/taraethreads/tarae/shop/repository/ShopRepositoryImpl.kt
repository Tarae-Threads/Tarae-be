package com.taraethreads.tarae.shop.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taraethreads.tarae.place.domain.QBrand
import com.taraethreads.tarae.place.domain.QTag
import com.taraethreads.tarae.shop.domain.QShop.shop
import com.taraethreads.tarae.shop.domain.QShopBrand
import com.taraethreads.tarae.shop.domain.QShopTag
import com.taraethreads.tarae.shop.domain.Shop
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ShopRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ShopRepositoryCustom {

    override fun findAllWithFilters(tagId: Long?, keyword: String?): List<Shop> {
        val query = queryFactory
            .selectDistinct(shop)
            .from(shop)
            .where(shop.active.isTrue)

        if (tagId != null) {
            val shopTag = QShopTag.shopTag
            val tag = QTag.tag
            query.join(shop.shopTags, shopTag)
                .join(shopTag.tag, tag)
                .where(tag.id.eq(tagId))
        }

        val tokens = keyword?.trim()?.split("\\s+".toRegex())?.filter { it.isNotEmpty() } ?: emptyList()
        if (tokens.isNotEmpty()) {
            val searchShopTag = QShopTag("searchShopTag")
            val searchTag = QTag("searchTag")
            val searchShopBrand = QShopBrand("searchShopBrand")
            val searchBrand = QBrand("searchBrand")
            query.leftJoin(shop.shopTags, searchShopTag)
                .leftJoin(searchShopTag.tag, searchTag)
            query.leftJoin(shop.shopBrands, searchShopBrand)
                .leftJoin(searchShopBrand.brand, searchBrand)

            tokens.forEach { token ->
                query.where(tokenCondition(token, searchTag, searchBrand))
            }
        }

        return query.fetch()
    }

    override fun findAllForAdmin(keyword: String?, pageable: Pageable): Page<Shop> {
        val tokens = keyword?.trim()?.split("\\s+".toRegex())?.filter { it.isNotEmpty() } ?: emptyList()

        fun applyKeyword(q: com.querydsl.jpa.impl.JPAQuery<*>) {
            tokens.forEach { token -> q.where(shop.name.containsIgnoreCase(token)) }
        }

        val totalCount = queryFactory
            .select(shop.countDistinct())
            .from(shop)
            .also { applyKeyword(it) }
            .fetchOne() ?: 0L

        val content = queryFactory
            .selectDistinct(shop)
            .from(shop)
            .also { applyKeyword(it) }
            .orderBy(shop.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(content, pageable, totalCount)
    }

    private fun tokenCondition(token: String, searchTag: QTag, searchBrand: QBrand): BooleanExpression =
        shop.name.containsIgnoreCase(token)
            .or(searchTag.name.containsIgnoreCase(token))
            .or(searchBrand.name.containsIgnoreCase(token))
}

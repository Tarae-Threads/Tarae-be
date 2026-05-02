package com.taraethreads.tarae.shop.domain

import com.taraethreads.tarae.place.domain.Category
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "shop_categories",
    uniqueConstraints = [UniqueConstraint(columnNames = ["shop_id", "category_id"])]
)
class ShopCategory(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    val shop: Shop,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShopCategory) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

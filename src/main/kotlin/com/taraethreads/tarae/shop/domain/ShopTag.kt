package com.taraethreads.tarae.shop.domain

import com.taraethreads.tarae.place.domain.Tag
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
    name = "shop_tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["shop_id", "tag_id"])]
)
class ShopTag(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    val shop: Shop,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    val tag: Tag,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShopTag) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

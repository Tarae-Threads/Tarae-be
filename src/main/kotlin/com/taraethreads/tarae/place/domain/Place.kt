package com.taraethreads.tarae.place.domain

import com.taraethreads.tarae.global.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "places")
class Place(
    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, length = 50)
    val region: String,

    @Column(nullable = false, length = 100)
    val district: String,

    @Column(nullable = false, length = 255)
    val address: String,

    @Column(precision = 10, scale = 7)
    val lat: BigDecimal? = null,

    @Column(precision = 10, scale = 7)
    val lng: BigDecimal? = null,

    @Column(length = 255)
    val hoursText: String? = null,

    @Column(length = 100)
    val closedDays: String? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(length = 255)
    val instagramUrl: String? = null,

    @Column(length = 255)
    val websiteUrl: String? = null,

    @Column(length = 255)
    val naverMapUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: PlaceStatus = PlaceStatus.OPEN,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "place_categories",
        joinColumns = [JoinColumn(name = "place_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    val categories: MutableList<Category> = mutableListOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "place_tags",
        joinColumns = [JoinColumn(name = "place_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableList<Tag> = mutableListOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "place_brands",
        joinColumns = [JoinColumn(name = "place_id")],
        inverseJoinColumns = [JoinColumn(name = "brand_id")]
    )
    val brands: MutableList<Brand> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Place) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

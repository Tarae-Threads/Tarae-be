package com.taraethreads.tarae.place.domain

import com.taraethreads.tarae.global.common.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize
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

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "place", cascade = [CascadeType.ALL], orphanRemoval = true)
    val placeCategories: MutableList<PlaceCategory> = mutableListOf()

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "place", cascade = [CascadeType.ALL], orphanRemoval = true)
    val placeTags: MutableList<PlaceTag> = mutableListOf()

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "place", cascade = [CascadeType.ALL], orphanRemoval = true)
    val placeBrands: MutableList<PlaceBrand> = mutableListOf()

    fun addCategory(category: Category) {
        placeCategories.add(PlaceCategory(place = this, category = category))
    }

    fun addTag(tag: Tag) {
        placeTags.add(PlaceTag(place = this, tag = tag))
    }

    fun addBrand(brand: Brand) {
        placeBrands.add(PlaceBrand(place = this, brand = brand))
    }

    val categories: List<Category> get() = placeCategories.map { it.category }
    val tags: List<Tag> get() = placeTags.map { it.tag }
    val brands: List<Brand> get() = placeBrands.map { it.brand }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Place) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

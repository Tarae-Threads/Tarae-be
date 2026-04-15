package com.taraethreads.tarae.place.domain

import com.taraethreads.tarae.admin.dto.PlaceCreateForm
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
    var name: String,

    @Column(nullable = false, length = 50)
    var region: String,

    @Column(nullable = false, length = 100)
    var district: String,

    @Column(nullable = false, length = 255)
    var address: String,

    @Column(precision = 10, scale = 7)
    var lat: BigDecimal? = null,

    @Column(precision = 10, scale = 7)
    var lng: BigDecimal? = null,

    @Column(length = 255)
    var hoursText: String? = null,

    @Column(length = 100)
    var closedDays: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(length = 255)
    var instagramUrl: String? = null,

    @Column(length = 255)
    var websiteUrl: String? = null,

    @Column(length = 255)
    var naverMapUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: PlaceStatus = PlaceStatus.OPEN,

    @Column(nullable = false)
    var active: Boolean = true,
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

    fun activate() {
        active = true
    }

    fun deactivate() {
        active = false
    }

    fun update(form: PlaceCreateForm) {
        name = form.name
        status = form.status
        region = form.region
        district = form.district
        address = form.address
        lat = form.lat
        lng = form.lng
        hoursText = form.hoursText
        closedDays = form.closedDays
        description = form.description
        instagramUrl = form.instagramUrl
        websiteUrl = form.websiteUrl
        naverMapUrl = form.naverMapUrl
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Place) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

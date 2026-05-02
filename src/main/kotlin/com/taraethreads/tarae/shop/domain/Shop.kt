package com.taraethreads.tarae.shop.domain

import com.taraethreads.tarae.admin.dto.ShopCreateForm
import com.taraethreads.tarae.global.common.BaseEntity
import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.domain.Tag
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.BatchSize

@Entity
@Table(name = "shops")
class Shop(
    @Column(nullable = false, length = 100)
    var name: String,

    @Column(length = 255)
    var instagramUrl: String? = null,

    @Column(length = 255)
    var naverUrl: String? = null,

    @Column(length = 255)
    var websiteUrl: String? = null,

    @Column(nullable = false)
    var active: Boolean = true,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "shop", cascade = [CascadeType.ALL], orphanRemoval = true)
    val shopCategories: MutableList<ShopCategory> = mutableListOf()

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "shop", cascade = [CascadeType.ALL], orphanRemoval = true)
    val shopTags: MutableList<ShopTag> = mutableListOf()

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "shop", cascade = [CascadeType.ALL], orphanRemoval = true)
    val shopBrands: MutableList<ShopBrand> = mutableListOf()

    fun addCategory(category: Category) {
        shopCategories.add(ShopCategory(shop = this, category = category))
    }

    fun addTag(tag: Tag) {
        shopTags.add(ShopTag(shop = this, tag = tag))
    }

    fun addBrand(brand: Brand) {
        shopBrands.add(ShopBrand(shop = this, brand = brand))
    }

    val categories: List<Category> get() = shopCategories.map { it.category }
    val tags: List<Tag> get() = shopTags.map { it.tag }
    val brands: List<Brand> get() = shopBrands.map { it.brand }

    fun activate() {
        active = true
    }

    fun deactivate() {
        active = false
    }

    fun update(form: ShopCreateForm) {
        name = form.name
        instagramUrl = form.instagramUrl
        naverUrl = form.naverUrl
        websiteUrl = form.websiteUrl
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shop) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

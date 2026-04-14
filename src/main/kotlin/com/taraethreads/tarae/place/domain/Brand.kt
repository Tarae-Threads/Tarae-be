package com.taraethreads.tarae.place.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "brands")
class Brand(
    name: String,
    type: BrandType,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false, length = 100)
    var name: String = name
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var type: BrandType = type
        protected set

    fun update(name: String, type: BrandType) {
        val trimmed = name.trim()
        require(trimmed.isNotBlank()) { "이름은 비어 있을 수 없습니다" }
        this.name = trimmed
        this.type = type
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Brand) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

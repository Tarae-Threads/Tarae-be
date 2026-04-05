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
    @Column(nullable = false, length = 100)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val type: BrandType,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Brand) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

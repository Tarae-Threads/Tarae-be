package com.taraethreads.tarae.place.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "categories")
class Category(
    name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(nullable = false, length = 50)
    var name: String = name
        protected set

    fun rename(name: String) {
        val trimmed = name.trim()
        require(trimmed.isNotBlank()) { "이름은 비어 있을 수 없습니다" }
        this.name = trimmed
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

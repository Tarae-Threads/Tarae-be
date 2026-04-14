package com.taraethreads.tarae.review.domain

import com.taraethreads.tarae.global.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "reviews",
    indexes = [Index(name = "idx_review_target", columnList = "targetType, targetId")],
)
class Review(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val targetType: ReviewTargetType,

    @Column(nullable = false)
    val targetId: Long,

    @Column(nullable = false, length = 50)
    val nickname: String,

    @Column(nullable = false, length = 100)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Review) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

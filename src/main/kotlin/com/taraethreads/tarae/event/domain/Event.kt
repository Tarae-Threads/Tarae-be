package com.taraethreads.tarae.event.domain

import com.taraethreads.tarae.global.common.BaseEntity
import com.taraethreads.tarae.place.domain.Place
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "events")
class Event(
    @Column(nullable = false, length = 200)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val eventType: EventType,

    @Column(nullable = false)
    val startDate: LocalDate,

    val endDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    val place: Place? = null,

    @Column(length = 255)
    val locationText: String? = null,

    @Column(precision = 10, scale = 7)
    val lat: BigDecimal? = null,

    @Column(precision = 10, scale = 7)
    val lng: BigDecimal? = null,

    @Column(columnDefinition = "TEXT")
    val links: String? = null,

    @Column(nullable = false)
    val active: Boolean = true,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

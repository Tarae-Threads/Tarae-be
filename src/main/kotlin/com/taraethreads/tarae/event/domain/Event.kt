package com.taraethreads.tarae.event.domain

import com.taraethreads.tarae.admin.dto.EventCreateForm
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
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var eventType: EventType,

    @Column(nullable = false)
    var startDate: LocalDate,

    var endDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    var place: Place? = null,

    @Column(length = 255)
    var locationText: String? = null,

    @Column(precision = 10, scale = 7)
    var lat: BigDecimal? = null,

    @Column(precision = 10, scale = 7)
    var lng: BigDecimal? = null,

    @Column(columnDefinition = "TEXT")
    var links: String? = null,

    @Column(nullable = false)
    var active: Boolean = true,
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    fun update(form: EventCreateForm) {
        title = form.title
        eventType = form.eventType
        startDate = form.startDate
        endDate = form.endDate
        locationText = form.locationText
        description = form.description
        lat = form.lat
        lng = form.lng
        links = form.links
    }

    fun activate() {
        active = true
    }

    fun deactivate() {
        active = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

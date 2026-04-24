package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface TagRepository : JpaRepository<Tag, Long> {
    fun existsByName(name: String): Boolean
    fun findAllByOrderById(): List<Tag>

    @Query("SELECT pt.tag.id, COUNT(pt) FROM PlaceTag pt GROUP BY pt.tag.id")
    fun countPlaceUsagesGrouped(): List<Array<Any>>

    @Query("SELECT COUNT(pt) FROM PlaceTag pt WHERE pt.tag.id = :tagId")
    fun countPlaceUsages(tagId: Long): Long

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PlaceTag pt WHERE pt.tag.id = :tagId")
    fun deletePlaceMappings(tagId: Long)
}

package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CategoryRepository : JpaRepository<Category, Long> {
    fun existsByName(name: String): Boolean
    fun findAllByOrderById(): List<Category>

    @Query("SELECT pc.category.id, COUNT(pc) FROM PlaceCategory pc GROUP BY pc.category.id")
    fun countPlaceUsagesGrouped(): List<Array<Any>>

    @Query("SELECT COUNT(pc) FROM PlaceCategory pc WHERE pc.category.id = :categoryId")
    fun countPlaceUsages(categoryId: Long): Long

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PlaceCategory pc WHERE pc.category.id = :categoryId")
    fun deletePlaceMappings(categoryId: Long)
}

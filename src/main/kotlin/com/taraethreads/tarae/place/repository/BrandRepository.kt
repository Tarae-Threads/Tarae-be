package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Brand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface BrandRepository : JpaRepository<Brand, Long> {
    fun existsByName(name: String): Boolean
    fun findAllByOrderById(): List<Brand>

    @Query("SELECT pb.brand.id, COUNT(pb) FROM PlaceBrand pb GROUP BY pb.brand.id")
    fun countPlaceUsagesGrouped(): List<Array<Any>>

    @Query("SELECT COUNT(pb) FROM PlaceBrand pb WHERE pb.brand.id = :brandId")
    fun countPlaceUsages(brandId: Long): Long

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PlaceBrand pb WHERE pb.brand.id = :brandId")
    fun deletePlaceMappings(brandId: Long)
}

package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Brand
import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<Brand, Long>

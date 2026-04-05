package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long>

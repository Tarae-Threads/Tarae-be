package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Long> {
    fun existsByName(name: String): Boolean
}

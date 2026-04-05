package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.place.domain.Place
import org.springframework.data.jpa.repository.JpaRepository

interface PlaceRepository : JpaRepository<Place, Long>, PlaceRepositoryCustom

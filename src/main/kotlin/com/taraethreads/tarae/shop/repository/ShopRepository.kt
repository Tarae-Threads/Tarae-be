package com.taraethreads.tarae.shop.repository

import com.taraethreads.tarae.shop.domain.Shop
import org.springframework.data.jpa.repository.JpaRepository

interface ShopRepository : JpaRepository<Shop, Long>, ShopRepositoryCustom

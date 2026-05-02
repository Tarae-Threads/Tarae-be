package com.taraethreads.tarae.admin.dto

data class ShopCreateForm(
    val name: String = "",
    val instagramUrl: String? = null,
    val naverUrl: String? = null,
    val websiteUrl: String? = null,
    val description: String? = null,
    val tagIds: List<Long> = emptyList(),
    val brandIds: List<Long> = emptyList(),
)

package com.taraethreads.tarae.admin.dto

data class ShopBulkCreateRequest(
    val shops: MutableList<ShopCreateForm> = mutableListOf(),
)

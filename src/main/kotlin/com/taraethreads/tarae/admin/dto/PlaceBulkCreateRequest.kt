package com.taraethreads.tarae.admin.dto

data class PlaceBulkCreateRequest(
    val places: MutableList<PlaceCreateForm> = mutableListOf(),
)

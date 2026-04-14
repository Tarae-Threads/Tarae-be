package com.taraethreads.tarae.admin.dto

data class EventBulkCreateRequest(
    val events: MutableList<EventCreateForm> = mutableListOf(),
)

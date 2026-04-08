package com.taraethreads.tarae.global.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class LongListJsonConverter : AttributeConverter<List<Long>, String> {

    private val objectMapper = ObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<Long>?): String? {
        if (attribute.isNullOrEmpty()) return null
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): List<Long> {
        if (dbData.isNullOrBlank()) return emptyList()
        return objectMapper.readValue(dbData, object : TypeReference<List<Long>>() {})
    }
}

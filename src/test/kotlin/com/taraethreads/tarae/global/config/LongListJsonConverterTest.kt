package com.taraethreads.tarae.global.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LongListJsonConverterTest {

    private val converter = LongListJsonConverter()

    @Nested
    inner class `convertToDatabaseColumn` {

        @Test
        fun `리스트를 JSON 배열 문자열로 변환한다`() {
            val result = converter.convertToDatabaseColumn(listOf(3L, 1L))
            assertThat(result).isEqualTo("[3,1]")
        }

        @Test
        fun `빈 리스트는 null을 반환한다`() {
            val result = converter.convertToDatabaseColumn(emptyList())
            assertThat(result).isNull()
        }

        @Test
        fun `null은 null을 반환한다`() {
            val result = converter.convertToDatabaseColumn(null)
            assertThat(result).isNull()
        }
    }

    @Nested
    inner class `convertToEntityAttribute` {

        @Test
        fun `JSON 배열 문자열을 리스트로 변환한다`() {
            val result = converter.convertToEntityAttribute("[3,1]")
            assertThat(result).containsExactly(3L, 1L)
        }

        @Test
        fun `null은 빈 리스트를 반환한다`() {
            val result = converter.convertToEntityAttribute(null)
            assertThat(result).isEmpty()
        }

        @Test
        fun `빈 문자열은 빈 리스트를 반환한다`() {
            val result = converter.convertToEntityAttribute("")
            assertThat(result).isEmpty()
        }
    }
}

package com.taraethreads.tarae.place.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class BrandTest {

    @Test
    @DisplayName("update는 이름과 타입을 변경한다")
    fun update() {
        val brand = Brand(name = "산네스간", type = BrandType.YARN)

        brand.update("  히야히야  ", BrandType.NEEDLE)

        assertThat(brand.name).isEqualTo("히야히야")
        assertThat(brand.type).isEqualTo(BrandType.NEEDLE)
    }

    @Test
    @DisplayName("update에 공백 이름을 넘기면 예외가 발생한다")
    fun updateBlank() {
        val brand = Brand(name = "산네스간", type = BrandType.YARN)

        assertThatThrownBy { brand.update("   ", BrandType.NEEDLE) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}

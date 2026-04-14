package com.taraethreads.tarae.place.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CategoryTest {

    @Test
    @DisplayName("rename은 이름을 변경하고 공백을 trim한다")
    fun rename() {
        // given
        val category = Category(name = "원본")

        // when
        category.rename("  새이름  ")

        // then
        assertThat(category.name).isEqualTo("새이름")
    }

    @Test
    @DisplayName("rename에 공백 문자열을 넘기면 예외가 발생한다")
    fun renameBlank() {
        val category = Category(name = "원본")

        assertThatThrownBy { category.rename("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}

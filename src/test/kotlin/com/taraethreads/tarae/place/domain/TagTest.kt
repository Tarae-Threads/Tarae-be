package com.taraethreads.tarae.place.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TagTest {

    @Test
    @DisplayName("rename은 이름을 변경하고 공백을 trim한다")
    fun rename() {
        val tag = Tag(name = "원본")

        tag.rename("  새이름  ")

        assertThat(tag.name).isEqualTo("새이름")
    }

    @Test
    @DisplayName("rename에 공백 문자열을 넘기면 예외가 발생한다")
    fun renameBlank() {
        val tag = Tag(name = "원본")

        assertThatThrownBy { tag.rename("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}

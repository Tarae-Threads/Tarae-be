package com.taraethreads.tarae.request.dto

import com.taraethreads.tarae.request.domain.RequestType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlaceRequestInputTest {

    @Nested
    inner class `toEntity 매핑` {

        @Test
        fun `새 필드 포함 시 엔티티에 모두 매핑된다`() {
            // given
            val input = PlaceRequestInput(
                requestType = RequestType.NEW,
                categoryIds = listOf(1L, 2L),
                categoryText = "뜨개카페",
                brandYarnIds = listOf(10L, 11L),
                brandsYarn = "직접입력실",
                brandNeedleIds = listOf(20L),
                brandsNeedle = null,
                brandNotionsIds = listOf(30L),
                brandsNotions = null,
                brandPatternbookIds = listOf(40L),
                brandsPatternbook = "직접입력도안",
            )

            // when
            val entity = input.toEntity()

            // then
            assertThat(entity.categoryText).isEqualTo("뜨개카페")
            assertThat(entity.brandYarnIds).containsExactly(10L, 11L)
            assertThat(entity.brandsYarn).isEqualTo("직접입력실")
            assertThat(entity.brandNeedleIds).containsExactly(20L)
            assertThat(entity.brandsNeedle).isNull()
            assertThat(entity.brandNotionsIds).containsExactly(30L)
            assertThat(entity.brandsNotions).isNull()
            assertThat(entity.brandPatternbookIds).containsExactly(40L)
            assertThat(entity.brandsPatternbook).isEqualTo("직접입력도안")
        }

        @Test
        fun `새 필드 미포함 시 기본값으로 매핑된다`() {
            // given
            val input = PlaceRequestInput(
                requestType = RequestType.NEW,
                name = "실과 바늘",
            )

            // when
            val entity = input.toEntity()

            // then
            assertThat(entity.categoryText).isNull()
            assertThat(entity.brandYarnIds).isEmpty()
            assertThat(entity.brandNeedleIds).isEmpty()
            assertThat(entity.brandNotionsIds).isEmpty()
            assertThat(entity.brandPatternbookIds).isEmpty()
            assertThat(entity.brandsPatternbook).isNull()
        }

        @Test
        fun `이메일 포함 시 엔티티에 매핑된다`() {
            // given
            val input = PlaceRequestInput(
                requestType = RequestType.NEW,
                email = "user@example.com",
            )

            // when
            val entity = input.toEntity()

            // then
            assertThat(entity.email).isEqualTo("user@example.com")
        }

        @Test
        fun `이메일 미포함 시 null로 매핑된다`() {
            // given
            val input = PlaceRequestInput(requestType = RequestType.NEW)

            // when
            val entity = input.toEntity()

            // then
            assertThat(entity.email).isNull()
        }
    }
}

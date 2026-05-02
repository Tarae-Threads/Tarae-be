package com.taraethreads.tarae.shop.domain

import com.taraethreads.tarae.admin.dto.ShopCreateForm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ShopTest {

    private fun shop() = Shop(
        name = "실뭉치샵",
        instagramUrl = "https://instagram.com/old",
        naverUrl = "https://smartstore.naver.com/old",
        websiteUrl = "https://old.com",
    )

    @Nested
    inner class `update` {

        @Test
        fun `폼 데이터로 온라인샵 정보를 업데이트한다`() {
            // given
            val shop = shop()
            val form = ShopCreateForm(
                name = "새실뭉치샵",
                instagramUrl = "https://instagram.com/new",
                naverUrl = "https://smartstore.naver.com/new",
                websiteUrl = "https://new.com",
            )

            // when
            shop.update(form)

            // then
            assertThat(shop.name).isEqualTo("새실뭉치샵")
            assertThat(shop.instagramUrl).isEqualTo("https://instagram.com/new")
            assertThat(shop.naverUrl).isEqualTo("https://smartstore.naver.com/new")
            assertThat(shop.websiteUrl).isEqualTo("https://new.com")
        }
    }

    @Nested
    inner class `activate deactivate` {

        @Test
        fun `deactivate 호출 시 active가 false가 된다`() {
            // given
            val shop = shop()

            // when
            shop.deactivate()

            // then
            assertThat(shop.active).isFalse()
        }

        @Test
        fun `activate 호출 시 active가 true가 된다`() {
            // given
            val shop = shop()
            shop.deactivate()

            // when
            shop.activate()

            // then
            assertThat(shop.active).isTrue()
        }
    }
}

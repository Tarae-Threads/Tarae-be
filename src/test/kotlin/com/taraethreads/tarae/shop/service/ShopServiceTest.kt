package com.taraethreads.tarae.shop.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.shop.domain.Shop
import com.taraethreads.tarae.shop.dto.ShopDetailResponse
import com.taraethreads.tarae.shop.dto.ShopListResponse
import com.taraethreads.tarae.shop.repository.ShopRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Optional

class ShopServiceTest {

    private val shopRepository: ShopRepository = mockk()
    private val shopService: ShopService = ShopService(shopRepository)

    private fun shop(name: String = "실뭉치샵") = Shop(name = name)

    @Nested
    inner class `목록 조회` {

        @Test
        fun `필터 파라미터를 repository에 그대로 전달하고 ShopListResponse 목록을 반환한다`() {
            // given
            every { shopRepository.findAllWithFilters(1L, null, null) } returns listOf(shop())

            // when
            val result: List<ShopListResponse> = shopService.getShops(categoryId = 1L, tagId = null, keyword = null)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("실뭉치샵")
            verify { shopRepository.findAllWithFilters(1L, null, null) }
        }

        @Test
        fun `필터 없이 전체 조회한다`() {
            // given
            every { shopRepository.findAllWithFilters(null, null, null) } returns emptyList()

            // when
            val result: List<ShopListResponse> = shopService.getShops(null, null, null)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `상세 조회` {

        @Test
        fun `존재하는 id로 조회하면 ShopDetailResponse를 반환한다`() {
            // given
            every { shopRepository.findById(1L) } returns Optional.of(shop())

            // when
            val result: ShopDetailResponse = shopService.getShop(1L)

            // then
            assertThat(result.name).isEqualTo("실뭉치샵")
        }

        @Test
        fun `존재하지 않는 id로 조회하면 SHOP_NOT_FOUND 예외가 발생한다`() {
            // given
            every { shopRepository.findById(999L) } returns Optional.empty()

            // when & then
            assertThatThrownBy { shopService.getShop(999L) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.SHOP_NOT_FOUND }
        }

        @Test
        fun `비활성화된 온라인샵 조회 시 SHOP_NOT_FOUND 예외가 발생한다`() {
            // given
            val inactiveShop = shop().apply { deactivate() }
            every { shopRepository.findById(1L) } returns Optional.of(inactiveShop)

            // when & then
            assertThatThrownBy { shopService.getShop(1L) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.SHOP_NOT_FOUND }
        }
    }
}

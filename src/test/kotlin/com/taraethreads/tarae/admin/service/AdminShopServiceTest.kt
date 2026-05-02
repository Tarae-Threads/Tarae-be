package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.ShopCreateForm
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.shop.domain.Shop
import com.taraethreads.tarae.shop.repository.ShopRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Optional

class AdminShopServiceTest {

    private val shopRepository: ShopRepository = mockk()
    private val shopAssociationSyncer: ShopAssociationSyncer = mockk()
    private val adminShopService: AdminShopService = AdminShopService(shopRepository, shopAssociationSyncer)

    private fun shop(name: String = "실뭉치샵") = Shop(name = name)

    @Nested
    inner class `toggleActive` {

        @Test
        fun `활성 상태인 샵을 비활성화한다`() {
            // given
            val activeShop = shop()
            every { shopRepository.findById(1L) } returns Optional.of(activeShop)

            // when
            adminShopService.toggleActive(1L)

            // then
            assertThat(activeShop.active).isFalse()
        }

        @Test
        fun `비활성 상태인 샵을 활성화한다`() {
            // given
            val inactiveShop = shop().apply { deactivate() }
            every { shopRepository.findById(1L) } returns Optional.of(inactiveShop)

            // when
            adminShopService.toggleActive(1L)

            // then
            assertThat(inactiveShop.active).isTrue()
        }
    }

    @Nested
    inner class `delete` {

        @Test
        fun `존재하는 샵을 삭제한다`() {
            // given
            val shop = shop()
            every { shopRepository.findById(1L) } returns Optional.of(shop)
            justRun { shopRepository.delete(shop) }

            // when
            adminShopService.delete(1L)

            // then
            verify { shopRepository.delete(shop) }
        }

        @Test
        fun `존재하지 않는 샵 삭제 시 SHOP_NOT_FOUND 예외가 발생한다`() {
            // given
            every { shopRepository.findById(999L) } returns Optional.empty()

            // when & then
            assertThatThrownBy { adminShopService.delete(999L) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.SHOP_NOT_FOUND }
        }
    }

    @Nested
    inner class `createBulk` {

        @Test
        fun `유효한 폼으로 온라인샵을 생성한다`() {
            // given
            val form = ShopCreateForm(name = "실뭉치샵")
            val savedShop = Shop(name = "실뭉치샵")
            every { shopRepository.save(any()) } returns savedShop
            justRun { shopAssociationSyncer.attach(any(), any()) }

            // when
            adminShopService.createBulk(listOf(form))

            // then
            verify { shopRepository.save(any()) }
            verify { shopAssociationSyncer.attach(any(), form) }
        }
    }
}

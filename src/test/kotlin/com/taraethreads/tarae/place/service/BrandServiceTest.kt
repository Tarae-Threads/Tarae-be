package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.repository.BrandRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Optional

class BrandServiceTest {

    private val brandRepository: BrandRepository = mockk()
    private val brandService: BrandService = BrandService(brandRepository)

    @Test
    fun `전체 브랜드 목록을 BrandResponse로 반환한다`() {
        // given
        every { brandRepository.findAll() } returns listOf(
            Brand(name = "산네스간", type = BrandType.YARN),
            Brand(name = "히야히야", type = BrandType.NEEDLE),
        )

        // when
        val result = brandService.getAll()

        // then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("산네스간", "히야히야")
        assertThat(result[0].type).isEqualTo("YARN")
    }

    @Test
    fun `create는 trim된 이름과 타입으로 브랜드를 저장한다`() {
        val saved = slot<Brand>()
        every { brandRepository.existsByName("산네스간") } returns false
        every { brandRepository.save(capture(saved)) } answers { saved.captured }

        brandService.create("  산네스간  ", BrandType.YARN)

        assertThat(saved.captured.name).isEqualTo("산네스간")
        assertThat(saved.captured.type).isEqualTo(BrandType.YARN)
    }

    @Test
    fun `create는 중복 이름이면 예외가 발생한다`() {
        every { brandRepository.existsByName("중복") } returns true

        assertThatThrownBy { brandService.create("중복", BrandType.YARN) }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.DUPLICATE_MASTER_NAME }
    }

    @Test
    fun `update는 이름과 타입을 변경한다`() {
        val brand = Brand(name = "기존", type = BrandType.YARN)
        every { brandRepository.findById(1L) } returns Optional.of(brand)
        every { brandRepository.existsByName("새이름") } returns false

        brandService.update(1L, "새이름", BrandType.NEEDLE)

        assertThat(brand.name).isEqualTo("새이름")
        assertThat(brand.type).isEqualTo(BrandType.NEEDLE)
    }

    @Test
    fun `update는 존재하지 않으면 BRAND_NOT_FOUND 예외가 발생한다`() {
        every { brandRepository.findById(99L) } returns Optional.empty()

        assertThatThrownBy { brandService.update(99L, "새이름", BrandType.YARN) }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.BRAND_NOT_FOUND }
    }

    @Test
    fun `update는 다른 이름과 중복이면 예외가 발생한다`() {
        val brand = Brand(name = "기존", type = BrandType.YARN)
        every { brandRepository.findById(1L) } returns Optional.of(brand)
        every { brandRepository.existsByName("중복") } returns true

        assertThatThrownBy { brandService.update(1L, "중복", BrandType.YARN) }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.DUPLICATE_MASTER_NAME }
    }

    @Test
    fun `delete는 매핑을 제거하고 브랜드를 삭제한다`() {
        val brand = Brand(name = "삭제대상", type = BrandType.YARN)
        every { brandRepository.findById(1L) } returns Optional.of(brand)
        every { brandRepository.deletePlaceMappings(1L) } returns Unit
        every { brandRepository.delete(brand) } returns Unit

        brandService.delete(1L)

        verify { brandRepository.deletePlaceMappings(1L) }
        verify { brandRepository.delete(brand) }
    }

    @Test
    fun `delete는 존재하지 않으면 BRAND_NOT_FOUND 예외가 발생한다`() {
        every { brandRepository.findById(99L) } returns Optional.empty()

        assertThatThrownBy { brandService.delete(99L) }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.BRAND_NOT_FOUND }
    }
}

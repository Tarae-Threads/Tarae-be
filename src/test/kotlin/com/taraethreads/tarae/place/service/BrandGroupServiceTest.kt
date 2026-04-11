package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.repository.BrandRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BrandGroupServiceTest {

    private val brandRepository: BrandRepository = mockk()
    private val brandService: BrandService = BrandService(brandRepository)

    @Test
    fun `브랜드를 타입별로 그룹핑하여 반환한다`() {
        // given
        every { brandRepository.findAll() } returns listOf(
            Brand(name = "산네스간", type = BrandType.YARN),
            Brand(name = "다루마", type = BrandType.YARN),
            Brand(name = "클로버", type = BrandType.NEEDLE),
        )

        // when
        val result = brandService.getBrandsGroupedByType()

        // then
        assertThat(result).hasSize(2)
        val yarnGroup = result.find { it.type == "YARN" }!!
        assertThat(yarnGroup.brands).hasSize(2)
        assertThat(yarnGroup.brands.map { it.name }).containsExactlyInAnyOrder("산네스간", "다루마")
        val needleGroup = result.find { it.type == "NEEDLE" }!!
        assertThat(needleGroup.brands).hasSize(1)
        assertThat(needleGroup.brands[0].name).isEqualTo("클로버")
    }

    @Test
    fun `브랜드가 없으면 빈 리스트를 반환한다`() {
        // given
        every { brandRepository.findAll() } returns emptyList()

        // when
        val result = brandService.getBrandsGroupedByType()

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `타입 순서는 enum 선언 순서를 따른다`() {
        // given
        every { brandRepository.findAll() } returns listOf(
            Brand(name = "클로버", type = BrandType.NEEDLE),
            Brand(name = "산네스간", type = BrandType.YARN),
        )

        // when
        val result = brandService.getBrandsGroupedByType()

        // then
        assertThat(result.map { it.type }).containsExactly("YARN", "NEEDLE")
    }
}

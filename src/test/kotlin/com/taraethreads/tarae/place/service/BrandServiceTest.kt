package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.repository.BrandRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
}

package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.place.domain.Tag
import com.taraethreads.tarae.place.repository.TagRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TagServiceTest {

    private val tagRepository: TagRepository = mockk()
    private val tagService: TagService = TagService(tagRepository)

    @Test
    fun `전체 태그 목록을 TagResponse로 반환한다`() {
        // given
        every { tagRepository.findAll() } returns listOf(Tag(name = "주차가능"), Tag(name = "반려동물동반"))

        // when
        val result = tagService.getAll()

        // then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("주차가능", "반려동물동반")
    }
}

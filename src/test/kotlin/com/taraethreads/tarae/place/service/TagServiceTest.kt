package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Tag
import com.taraethreads.tarae.place.repository.TagRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Optional

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

    @Test
    fun `create는 trim된 이름으로 태그를 저장한다`() {
        val saved = slot<Tag>()
        every { tagRepository.existsByName("주차가능") } returns false
        every { tagRepository.save(capture(saved)) } answers { saved.captured }

        tagService.create("  주차가능  ")

        assertThat(saved.captured.name).isEqualTo("주차가능")
    }

    @Test
    fun `create는 중복 이름이면 예외가 발생한다`() {
        every { tagRepository.existsByName("중복") } returns true

        assertThatThrownBy { tagService.create("중복") }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.DUPLICATE_MASTER_NAME }
    }

    @Test
    fun `rename은 이름을 변경한다`() {
        val tag = Tag(name = "기존")
        every { tagRepository.findById(1L) } returns Optional.of(tag)
        every { tagRepository.existsByName("새이름") } returns false

        tagService.rename(1L, "새이름")

        assertThat(tag.name).isEqualTo("새이름")
    }

    @Test
    fun `rename은 존재하지 않으면 TAG_NOT_FOUND 예외가 발생한다`() {
        every { tagRepository.findById(99L) } returns Optional.empty()

        assertThatThrownBy { tagService.rename(99L, "새이름") }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.TAG_NOT_FOUND }
    }

    @Test
    fun `rename은 다른 이름과 중복이면 예외가 발생한다`() {
        val tag = Tag(name = "기존")
        every { tagRepository.findById(1L) } returns Optional.of(tag)
        every { tagRepository.existsByName("중복") } returns true

        assertThatThrownBy { tagService.rename(1L, "중복") }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.DUPLICATE_MASTER_NAME }
    }
}

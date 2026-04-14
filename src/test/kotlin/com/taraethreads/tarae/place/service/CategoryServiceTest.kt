package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.repository.CategoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Optional

class CategoryServiceTest {

    private val categoryRepository: CategoryRepository = mockk()
    private val categoryService = CategoryService(categoryRepository)

    @Test
    fun `create는 trim된 이름으로 카테고리를 저장한다`() {
        // given
        val saved = slot<Category>()
        every { categoryRepository.existsByName("뜨개샵") } returns false
        every { categoryRepository.save(capture(saved)) } answers { saved.captured }

        // when
        categoryService.create("  뜨개샵  ")

        // then
        assertThat(saved.captured.name).isEqualTo("뜨개샵")
        verify { categoryRepository.save(any()) }
    }

    @Test
    fun `create는 중복 이름이면 DUPLICATE_MASTER_NAME 예외가 발생한다`() {
        every { categoryRepository.existsByName("중복") } returns true

        assertThatThrownBy { categoryService.create("중복") }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.DUPLICATE_MASTER_NAME }
    }

    @Test
    fun `rename은 이름을 변경한다`() {
        val category = Category(name = "기존")
        every { categoryRepository.findById(1L) } returns Optional.of(category)
        every { categoryRepository.existsByName("새이름") } returns false

        categoryService.rename(1L, "새이름")

        assertThat(category.name).isEqualTo("새이름")
    }

    @Test
    fun `rename은 동일 이름이면 중복 체크를 하지 않는다`() {
        val category = Category(name = "기존")
        every { categoryRepository.findById(1L) } returns Optional.of(category)

        categoryService.rename(1L, "기존")

        assertThat(category.name).isEqualTo("기존")
        verify(exactly = 0) { categoryRepository.existsByName(any()) }
    }

    @Test
    fun `rename은 존재하지 않으면 CATEGORY_NOT_FOUND 예외가 발생한다`() {
        every { categoryRepository.findById(99L) } returns Optional.empty()

        assertThatThrownBy { categoryService.rename(99L, "새이름") }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.CATEGORY_NOT_FOUND }
    }

    @Test
    fun `rename은 다른 이름과 중복이면 예외가 발생한다`() {
        val category = Category(name = "기존")
        every { categoryRepository.findById(1L) } returns Optional.of(category)
        every { categoryRepository.existsByName("중복") } returns true

        assertThatThrownBy { categoryService.rename(1L, "중복") }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.DUPLICATE_MASTER_NAME }
    }
}

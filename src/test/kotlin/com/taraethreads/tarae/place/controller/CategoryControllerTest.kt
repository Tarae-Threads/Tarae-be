package com.taraethreads.tarae.place.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.global.exception.GlobalExceptionHandler
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.service.CategoryService
import io.mockk.every
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(
    controllers = [CategoryController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class CategoryControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var categoryService: CategoryService

    @Nested
    inner class `GET 카테고리 목록 조회` {

        @Test
        fun `200과 카테고리 목록을 반환한다`() {
            // given
            every { categoryService.getCategories() } returns listOf(
                Category("뜨개카페"),
                Category("털실가게"),
            )

            // when & then
            mockMvc.get("/api/categories").andExpect {
                status { isOk() }
                jsonPath("$.data[0].name") { value("뜨개카페") }
                jsonPath("$.data[1].name") { value("털실가게") }
            }
        }

        @Test
        fun `카테고리가 없으면 빈 배열을 반환한다`() {
            // given
            every { categoryService.getCategories() } returns emptyList()

            // when & then
            mockMvc.get("/api/categories").andExpect {
                status { isOk() }
                jsonPath("$.data") { isArray() }
                jsonPath("$.data") { isEmpty() }
            }
        }
    }
}

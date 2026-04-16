package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.domain.Tag
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import io.mockk.every
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [AdminMasterApiController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AdminMasterApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var categoryService: CategoryService

    @MockkBean
    lateinit var tagService: TagService

    @MockkBean
    lateinit var brandService: BrandService

    @Nested
    inner class `POST 카테고리 빠른 추가` {

        @Test
        fun `새 카테고리를 생성하고 JSON으로 반환한다`() {
            // given
            val category = Category(name = "부자재")
            setEntityId(category, 5L)
            every { categoryService.create("부자재") } returns category

            // when & then
            mockMvc.post("/admin/api/categories") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("name", "부자재")
            }.andExpect {
                status { isOk() }
                jsonPath("$.id") { value(5) }
                jsonPath("$.name") { value("부자재") }
            }
        }

        @Test
        fun `중복 카테고리명이면 400을 반환한다`() {
            // given
            every { categoryService.create("뜨개샵") } throws CustomException(ErrorCode.DUPLICATE_MASTER_NAME)

            // when & then
            mockMvc.post("/admin/api/categories") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("name", "뜨개샵")
            }.andExpect {
                status { isBadRequest() }
            }
        }

        @Test
        fun `빈 이름이면 400을 반환한다`() {
            // when & then
            mockMvc.post("/admin/api/categories") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("name", "   ")
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }

    @Nested
    inner class `POST 태그 빠른 추가` {

        @Test
        fun `새 태그를 생성하고 JSON으로 반환한다`() {
            // given
            val tag = Tag(name = "주차가능")
            setEntityId(tag, 10L)
            every { tagService.create("주차가능") } returns tag

            // when & then
            mockMvc.post("/admin/api/tags") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("name", "주차가능")
            }.andExpect {
                status { isOk() }
                jsonPath("$.id") { value(10) }
                jsonPath("$.name") { value("주차가능") }
            }
        }
    }

    @Nested
    inner class `POST 브랜드 빠른 추가` {

        @Test
        fun `새 브랜드를 생성하고 JSON으로 반환한다`() {
            // given
            val brand = Brand(name = "로완", type = BrandType.YARN)
            setEntityId(brand, 20L)
            every { brandService.create("로완", BrandType.YARN) } returns brand

            // when & then
            mockMvc.post("/admin/api/brands") {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
                param("name", "로완")
                param("type", "YARN")
            }.andExpect {
                status { isOk() }
                jsonPath("$.id") { value(20) }
                jsonPath("$.name") { value("로완") }
                jsonPath("$.type") { value("YARN") }
            }
        }
    }

    private fun setEntityId(entity: Any, id: Long) {
        val idField = entity.javaClass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(entity, id)
    }
}

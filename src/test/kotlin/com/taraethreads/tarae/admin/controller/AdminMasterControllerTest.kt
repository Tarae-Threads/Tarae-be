package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.domain.Tag
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [AdminMasterController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AdminMasterControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var categoryService: CategoryService

    @MockkBean
    lateinit var tagService: TagService

    @MockkBean
    lateinit var brandService: BrandService

    private fun stubLists() {
        every { categoryService.getAllForAdmin() } returns emptyList()
        every { tagService.getAllForAdmin() } returns emptyList()
        every { brandService.getAllForAdmin() } returns emptyList()
    }

    @Test
    fun `GET 마스터 목록은 200을 반환한다`() {
        stubLists()

        mockMvc.get("/admin/masters").andExpect {
            status { isOk() }
            view { name("admin/masters/list") }
        }
    }

    @Test
    fun `GET 태그 탭은 200을 반환한다`() {
        stubLists()

        mockMvc.get("/admin/masters?type=tags").andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `POST 카테고리 생성은 리다이렉트한다`() {
        every { categoryService.create("뜨개샵") } returns Category(name = "뜨개샵")

        mockMvc.post("/admin/masters/categories") {
            param("name", "뜨개샵")
        }.andExpect {
            status { is3xxRedirection() }
            redirectedUrl("/admin/masters?type=categories")
        }
    }

    @Test
    fun `POST 카테고리 수정은 리다이렉트한다`() {
        justRun { categoryService.rename(1L, "새이름") }

        mockMvc.post("/admin/masters/categories/1") {
            param("name", "새이름")
        }.andExpect {
            status { is3xxRedirection() }
        }
    }

    @Test
    fun `POST 태그 생성은 리다이렉트한다`() {
        every { tagService.create("주차가능") } returns Tag(name = "주차가능")

        mockMvc.post("/admin/masters/tags") {
            param("name", "주차가능")
        }.andExpect {
            status { is3xxRedirection() }
            redirectedUrl("/admin/masters?type=tags")
        }
    }

    @Test
    fun `POST 태그 수정은 리다이렉트한다`() {
        justRun { tagService.rename(1L, "새이름") }

        mockMvc.post("/admin/masters/tags/1") {
            param("name", "새이름")
        }.andExpect {
            status { is3xxRedirection() }
        }
    }

    @Test
    fun `POST 브랜드 생성은 리다이렉트한다`() {
        every { brandService.create("산네스간", BrandType.YARN) } returns Brand(name = "산네스간", type = BrandType.YARN)

        mockMvc.post("/admin/masters/brands") {
            param("name", "산네스간")
            param("type", "YARN")
        }.andExpect {
            status { is3xxRedirection() }
            redirectedUrl("/admin/masters?type=brands")
        }
    }

    @Test
    fun `POST 브랜드 수정은 리다이렉트한다`() {
        justRun { brandService.update(1L, "새이름", BrandType.NEEDLE) }

        mockMvc.post("/admin/masters/brands/1") {
            param("name", "새이름")
            param("type", "NEEDLE")
        }.andExpect {
            status { is3xxRedirection() }
        }
    }

    @Test
    fun `POST 카테고리 삭제는 리다이렉트한다`() {
        justRun { categoryService.delete(1L) }

        mockMvc.post("/admin/masters/categories/1/delete").andExpect {
            status { is3xxRedirection() }
            redirectedUrl("/admin/masters?type=categories")
        }
    }

    @Test
    fun `POST 태그 삭제는 리다이렉트한다`() {
        justRun { tagService.delete(1L) }

        mockMvc.post("/admin/masters/tags/1/delete").andExpect {
            status { is3xxRedirection() }
            redirectedUrl("/admin/masters?type=tags")
        }
    }

    @Test
    fun `POST 브랜드 삭제는 리다이렉트한다`() {
        justRun { brandService.delete(1L) }

        mockMvc.post("/admin/masters/brands/1/delete").andExpect {
            status { is3xxRedirection() }
            redirectedUrl("/admin/masters?type=brands")
        }
    }

}

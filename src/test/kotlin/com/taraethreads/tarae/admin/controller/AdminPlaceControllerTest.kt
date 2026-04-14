package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.admin.service.AdminPlaceService
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [AdminPlaceController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AdminPlaceControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminPlaceService: AdminPlaceService

    @MockkBean
    lateinit var categoryService: CategoryService

    @MockkBean
    lateinit var tagService: TagService

    @MockkBean
    lateinit var brandService: BrandService

    @Test
    fun `GET 목록은 200을 반환한다`() {
        every { adminPlaceService.list(null, any()) } returns PageImpl(emptyList(), PageRequest.of(0, 20), 0)

        mockMvc.get("/admin/places").andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `POST 토글은 302 리다이렉트한다`() {
        justRun { adminPlaceService.toggleActive(1L) }

        mockMvc.post("/admin/places/1/toggle-active").andExpect {
            status { is3xxRedirection() }
        }
    }

    @Test
    fun `POST 삭제는 302 리다이렉트한다`() {
        justRun { adminPlaceService.delete(1L) }

        mockMvc.post("/admin/places/1/delete").andExpect {
            status { is3xxRedirection() }
        }
    }
}

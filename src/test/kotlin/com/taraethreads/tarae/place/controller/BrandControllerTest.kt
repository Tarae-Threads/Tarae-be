package com.taraethreads.tarae.place.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.global.exception.GlobalExceptionHandler
import com.taraethreads.tarae.place.dto.BrandGroupResponse
import com.taraethreads.tarae.place.service.BrandService

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
    controllers = [BrandController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class BrandControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var brandService: BrandService

    @Nested
    inner class `GET 타입별 브랜드 목록 조회` {

        @Test
        fun `200과 타입별 그룹핑된 브랜드 목록을 반환한다`() {
            // given
            every { brandService.getBrandsGroupedByType() } returns listOf(
                BrandGroupResponse.BrandTypeGroup(
                    type = "YARN",
                    brands = listOf(
                        BrandGroupResponse.BrandItem(id = 1L, name = "산네스간"),
                        BrandGroupResponse.BrandItem(id = 2L, name = "다루마"),
                    ),
                ),
                BrandGroupResponse.BrandTypeGroup(
                    type = "NEEDLE",
                    brands = listOf(
                        BrandGroupResponse.BrandItem(id = 3L, name = "클로버"),
                    ),
                ),
            )

            // when & then
            mockMvc.get("/api/brands").andExpect {
                status { isOk() }
                jsonPath("$.data[0].type") { value("YARN") }
                jsonPath("$.data[0].brands[0].name") { value("산네스간") }
                jsonPath("$.data[1].type") { value("NEEDLE") }
                jsonPath("$.data[1].brands[0].name") { value("클로버") }
            }
        }

        @Test
        fun `브랜드가 없으면 빈 배열을 반환한다`() {
            // given
            every { brandService.getBrandsGroupedByType() } returns emptyList()

            // when & then
            mockMvc.get("/api/brands").andExpect {
                status { isOk() }
                jsonPath("$.data") { isArray() }
                jsonPath("$.data") { isEmpty() }
            }
        }
    }
}

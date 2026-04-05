package com.taraethreads.tarae.place.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.global.exception.GlobalExceptionHandler
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.service.PlaceService
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
    controllers = [PlaceController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class PlaceControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var placeService: PlaceService

    private fun place(
        name: String = "실과 바늘",
        region: String = "서울",
        district: String = "성수",
    ) = Place(name = name, region = region, district = district, address = "서울 성동구 테스트로 1")

    @Nested
    inner class `GET 목록 조회` {

        @Test
        fun `200과 장소 목록을 반환한다`() {
            // given
            every { placeService.getPlaces(null, null, null) } returns listOf(place())

            // when & then
            mockMvc.get("/api/places").andExpect {
                status { isOk() }
                jsonPath("$.data[0].name") { value("실과 바늘") }
                jsonPath("$.data[0].region") { value("서울") }
                jsonPath("$.data[0].district") { value("성수") }
                jsonPath("$.data[0].status") { value("OPEN") }
                jsonPath("$.data[0].categories") { isArray() }
                jsonPath("$.data[0].tags") { isArray() }
            }
        }

        @Test
        fun `region 파라미터를 서비스에 전달한다`() {
            // given
            every { placeService.getPlaces("서울", null, null) } returns listOf(place())

            // when & then
            mockMvc.get("/api/places?region=서울").andExpect {
                status { isOk() }
            }
        }

        @Test
        fun `categoryId, tagId 파라미터를 서비스에 전달한다`() {
            // given
            every { placeService.getPlaces(null, 1L, 2L) } returns emptyList()

            // when & then
            mockMvc.get("/api/places?categoryId=1&tagId=2").andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class `GET 상세 조회` {

        @Test
        fun `200과 장소 상세를 반환한다`() {
            // given
            every { placeService.getPlace(1L) } returns place()

            // when & then
            mockMvc.get("/api/places/1").andExpect {
                status { isOk() }
                jsonPath("$.data.name") { value("실과 바늘") }
                jsonPath("$.data.region") { value("서울") }
                jsonPath("$.data.district") { value("성수") }
                jsonPath("$.data.status") { value("OPEN") }
                jsonPath("$.data.categories") { isArray() }
                jsonPath("$.data.tags") { isArray() }
                jsonPath("$.data.brands") { isArray() }
            }
        }

        @Test
        fun `존재하지 않는 id 요청 시 404를 반환한다`() {
            // given
            every { placeService.getPlace(999L) } throws CustomException(ErrorCode.PLACE_NOT_FOUND)

            // when & then
            mockMvc.get("/api/places/999").andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("PLACE_NOT_FOUND") }
                jsonPath("$.status") { value(404) }
            }
        }
    }
}

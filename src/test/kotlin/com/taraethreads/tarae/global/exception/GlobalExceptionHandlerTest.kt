package com.taraethreads.tarae.global.exception

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test-exceptions")
class TestExceptionController {

    data class TestRequest(@field:NotBlank val name: String?)

    @GetMapping("/not-found")
    fun notFound(): Nothing = throw NoSuchElementException("리소스를 찾을 수 없습니다")

    @GetMapping("/server-error")
    fun serverError(): Nothing = throw RuntimeException("서버 오류")

    @PostMapping("/validation")
    fun validation(@Valid @RequestBody request: TestRequest): String = request.name!!
}

@WebMvcTest(
    controllers = [TestExceptionController::class, GlobalExceptionHandler::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class]
)
class GlobalExceptionHandlerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Nested
    inner class `Validation 실패` {
        @Test
        fun `@Valid 검증 실패 시 400을 반환한다`() {
            // given
            val invalidBody = objectMapper.writeValueAsString(mapOf("name" to ""))

            // when & then
            mockMvc.post("/test-exceptions/validation") {
                contentType = MediaType.APPLICATION_JSON
                content = invalidBody
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.code") { value("INVALID_INPUT") }
                jsonPath("$.status") { value(400) }
                jsonPath("$.message") { exists() }
            }
        }
    }

    @Nested
    inner class `리소스 없음` {
        @Test
        fun `NoSuchElementException 발생 시 404를 반환한다`() {
            // when & then
            mockMvc.get("/test-exceptions/not-found").andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("NOT_FOUND") }
                jsonPath("$.status") { value(404) }
                jsonPath("$.message") { exists() }
            }
        }
    }

    @Nested
    inner class `서버 오류` {
        @Test
        fun `처리되지 않은 예외 발생 시 500을 반환한다`() {
            // when & then
            mockMvc.get("/test-exceptions/server-error").andExpect {
                status { isInternalServerError() }
                jsonPath("$.code") { value("INTERNAL_ERROR") }
                jsonPath("$.status") { value(500) }
                jsonPath("$.message") { exists() }
            }
        }
    }
}

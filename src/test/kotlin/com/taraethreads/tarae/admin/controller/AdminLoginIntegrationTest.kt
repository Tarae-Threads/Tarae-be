package com.taraethreads.tarae.admin.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminLoginIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Nested
    inner class `미인증 접근` {

        @Test
        fun `인증 없이 관리자 대시보드 접근 시 로그인 페이지로 리다이렉트된다`() {
            mockMvc.get("/admin").andExpect {
                status { is3xxRedirection() }
                redirectedUrlPattern("**/admin/login")
            }
        }

        @Test
        fun `로그인 페이지는 인증 없이 접근 가능하다`() {
            mockMvc.get("/admin/login").andExpect {
                status { isOk() }
            }
        }
    }

    @Nested
    inner class `로그인 시도` {

        @Test
        fun `올바른 자격증명으로 로그인 시 대시보드로 리다이렉트된다`() {
            mockMvc.post("/admin/login") {
                with(csrf())
                param("username", "test-admin")
                param("password", "test-password")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin")
            }
        }

        @Test
        fun `잘못된 비밀번호로 로그인 시 로그인 페이지로 리다이렉트된다`() {
            mockMvc.post("/admin/login") {
                with(csrf())
                param("username", "test-admin")
                param("password", "wrong-password")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/login?error=true")
            }
        }
    }

    @Nested
    inner class `로그아웃` {

        @Test
        fun `로그아웃 시 로그인 페이지로 리다이렉트된다`() {
            mockMvc.post("/admin/logout") {
                with(csrf())
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/login")
            }
        }
    }

    @Nested
    inner class `API 경로 CSRF 제외` {

        @Test
        fun `API 경로는 CSRF 토큰 없이 POST해도 403이 아니다`() {
            // GET은 원래 CSRF 검증 대상이 아님 — POST로 CSRF 제외 설정 실효성 검증
            val result = mockMvc.post("/api/places").andReturn()
            assertThat(result.response.status).isNotEqualTo(403)
        }
    }
}

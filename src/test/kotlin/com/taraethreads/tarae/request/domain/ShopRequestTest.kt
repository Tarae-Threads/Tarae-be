package com.taraethreads.tarae.request.domain

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ShopRequestTest {

    private fun shopRequest() = ShopRequest(
        requestType = RequestType.NEW,
        name = "실뭉치샵",
    )

    @Nested
    inner class `approve` {

        @Test
        fun `PENDING 상태에서 approve 호출 시 APPROVED가 된다`() {
            // given
            val request = shopRequest()

            // when
            request.approve()

            // then
            assertThat(request.status).isEqualTo(RequestStatus.APPROVED)
        }

        @Test
        fun `이미 처리된 요청에 approve 호출 시 예외가 발생한다`() {
            // given
            val request = shopRequest()
            request.approve()

            // when & then
            assertThatThrownBy { request.approve() }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.REQUEST_ALREADY_PROCESSED }
        }
    }

    @Nested
    inner class `reject` {

        @Test
        fun `PENDING 상태에서 reject 호출 시 REJECTED가 된다`() {
            // given
            val request = shopRequest()

            // when
            request.reject()

            // then
            assertThat(request.status).isEqualTo(RequestStatus.REJECTED)
        }

        @Test
        fun `이미 처리된 요청에 reject 호출 시 예외가 발생한다`() {
            // given
            val request = shopRequest()
            request.reject()

            // when & then
            assertThatThrownBy { request.reject() }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.REQUEST_ALREADY_PROCESSED }
        }
    }
}

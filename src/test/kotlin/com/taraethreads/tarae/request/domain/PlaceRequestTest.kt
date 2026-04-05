package com.taraethreads.tarae.request.domain

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlaceRequestTest {

    private fun pendingRequest() = PlaceRequest(
        requestType = RequestType.NEW,
        name = "실과 바늘",
        address = "서울 성동구 테스트로 1",
    )

    @Nested
    inner class `approve 승인 처리` {

        @Test
        fun `PENDING 상태에서 승인하면 APPROVED 로 변경된다`() {
            // given
            val request = pendingRequest()
            assertThat(request.status).isEqualTo(RequestStatus.PENDING)

            // when
            request.approve()

            // then
            assertThat(request.status).isEqualTo(RequestStatus.APPROVED)
        }

        @Test
        fun `APPROVED 상태에서 승인하면 예외가 발생한다`() {
            // given
            val request = pendingRequest()
            request.approve()

            // when & then
            assertThatThrownBy { request.approve() }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }

        @Test
        fun `REJECTED 상태에서 승인하면 예외가 발생한다`() {
            // given
            val request = pendingRequest()
            request.reject()

            // when & then
            assertThatThrownBy { request.approve() }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }
    }

    @Nested
    inner class `reject 거절 처리` {

        @Test
        fun `PENDING 상태에서 거절하면 REJECTED 로 변경된다`() {
            // given
            val request = pendingRequest()

            // when
            request.reject()

            // then
            assertThat(request.status).isEqualTo(RequestStatus.REJECTED)
        }

        @Test
        fun `APPROVED 상태에서 거절하면 예외가 발생한다`() {
            // given
            val request = pendingRequest()
            request.approve()

            // when & then
            assertThatThrownBy { request.reject() }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }

        @Test
        fun `REJECTED 상태에서 거절하면 예외가 발생한다`() {
            // given
            val request = pendingRequest()
            request.reject()

            // when & then
            assertThatThrownBy { request.reject() }
                .isInstanceOf(CustomException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.REQUEST_ALREADY_PROCESSED)
        }
    }
}

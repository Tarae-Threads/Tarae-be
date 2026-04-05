package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.PlaceRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.Optional

class PlaceServiceTest {

    private val placeRepository: PlaceRepository = mockk()
    private val placeService: PlaceService = PlaceService(placeRepository)

    @Nested
    inner class `목록 조회` {

        @Test
        fun `필터 파라미터를 repository에 그대로 전달한다`() {
            // given
            val places = listOf(Place(name = "장소A", region = "서울", district = "성수", address = "서울 성동구"))
            every { placeRepository.findAllWithFilters("서울", 1L, null) } returns places

            // when
            val result = placeService.getPlaces(region = "서울", categoryId = 1L, tagId = null)

            // then
            assertThat(result).hasSize(1)
            verify { placeRepository.findAllWithFilters("서울", 1L, null) }
        }

        @Test
        fun `필터 없이 전체 조회한다`() {
            // given
            every { placeRepository.findAllWithFilters(null, null, null) } returns emptyList()

            // when
            val result = placeService.getPlaces(null, null, null)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    inner class `상세 조회` {

        @Test
        fun `존재하는 id로 조회하면 Place를 반환한다`() {
            // given
            val place = Place(name = "장소A", region = "서울", district = "성수", address = "서울 성동구")
            every { placeRepository.findById(1L) } returns Optional.of(place)

            // when
            val result = placeService.getPlace(1L)

            // then
            assertThat(result.name).isEqualTo("장소A")
        }

        @Test
        fun `존재하지 않는 id로 조회하면 PLACE_NOT_FOUND 예외가 발생한다`() {
            // given
            every { placeRepository.findById(999L) } returns Optional.empty()

            // when & then
            assertThatThrownBy { placeService.getPlace(999L) }
                .isInstanceOf(CustomException::class.java)
                .matches { (it as CustomException).errorCode == ErrorCode.PLACE_NOT_FOUND }
        }
    }
}

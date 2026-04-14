package com.taraethreads.tarae.place.domain

import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PlaceTest {

    private fun place() = Place(
        name = "실과 바늘",
        region = "서울",
        district = "성수",
        address = "서울 성동구 성수이로 1",
        lat = BigDecimal("37.5443"),
        lng = BigDecimal("127.0557"),
        hoursText = "10:00-18:00",
        closedDays = "일요일",
        description = "뜨개 편집샵",
        instagramUrl = "https://instagram.com/old",
        websiteUrl = "https://old.com",
        naverMapUrl = "https://naver.me/old",
    )

    @Nested
    inner class `update` {

        @Test
        fun `폼 데이터로 장소 정보를 업데이트한다`() {
            // given
            val place = place()
            val form = PlaceCreateForm(
                name = "실과 바늘 2호점",
                region = "서울",
                district = "강남",
                address = "서울 강남구 역삼동 1",
                lat = BigDecimal("37.4967"),
                lng = BigDecimal("127.0277"),
                hoursText = "11:00-20:00",
                closedDays = "월요일",
                description = "뜨개 편집샵 2호점",
                instagramUrl = "https://instagram.com/new",
                websiteUrl = "https://new.com",
                naverMapUrl = "https://naver.me/new",
            )

            // when
            place.update(form)

            // then
            assertThat(place.name).isEqualTo("실과 바늘 2호점")
            assertThat(place.region).isEqualTo("서울")
            assertThat(place.district).isEqualTo("강남")
            assertThat(place.address).isEqualTo("서울 강남구 역삼동 1")
            assertThat(place.lat).isEqualByComparingTo(BigDecimal("37.4967"))
            assertThat(place.lng).isEqualByComparingTo(BigDecimal("127.0277"))
            assertThat(place.hoursText).isEqualTo("11:00-20:00")
            assertThat(place.closedDays).isEqualTo("월요일")
            assertThat(place.description).isEqualTo("뜨개 편집샵 2호점")
            assertThat(place.instagramUrl).isEqualTo("https://instagram.com/new")
            assertThat(place.websiteUrl).isEqualTo("https://new.com")
            assertThat(place.naverMapUrl).isEqualTo("https://naver.me/new")
        }
    }

    @Nested
    inner class `activate deactivate` {
        @Test
        fun `deactivate 호출 시 active 가 false 가 된다`() {
            val place = place()
            place.deactivate()
            assertThat(place.active).isFalse()
        }

        @Test
        fun `activate 호출 시 active 가 true 가 된다`() {
            val place = place()
            place.deactivate()
            place.activate()
            assertThat(place.active).isTrue()
        }
    }
}

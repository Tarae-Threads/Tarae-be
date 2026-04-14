package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.BrandRepository
import com.taraethreads.tarae.place.repository.CategoryRepository
import com.taraethreads.tarae.place.repository.PlaceRepository
import com.taraethreads.tarae.place.repository.TagRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.Optional

class AdminPlaceServiceTest {

    private val placeRepository: PlaceRepository = mockk()
    private val categoryRepository: CategoryRepository = mockk(relaxed = true)
    private val tagRepository: TagRepository = mockk(relaxed = true)
    private val brandRepository: BrandRepository = mockk(relaxed = true)
    private val service = AdminPlaceService(placeRepository, categoryRepository, tagRepository, brandRepository)

    private fun place(name: String = "장소") = Place(name = name, region = "서울", district = "성수", address = "서울 성동구")

    @Test
    fun `createBulk 는 모든 폼을 저장한다`() {
        val saved = slot<Place>()
        every { placeRepository.save(capture(saved)) } answers { saved.captured }

        val ids = service.createBulk(
            listOf(
                PlaceCreateForm(name = "장소1", region = "서울", district = "성수", address = "주소1"),
                PlaceCreateForm(name = "장소2", region = "부산", district = "해운대", address = "주소2"),
            )
        )

        assertThat(ids).hasSize(2)
        verify(exactly = 2) { placeRepository.save(any()) }
    }

    @Test
    fun `toggleActive 는 active 상태를 뒤집는다`() {
        val place = place()
        every { placeRepository.findById(1L) } returns Optional.of(place)

        service.toggleActive(1L)

        assertThat(place.active).isFalse()
    }

    @Test
    fun `delete 는 존재하는 장소를 삭제한다`() {
        val place = place()
        every { placeRepository.findById(1L) } returns Optional.of(place)
        every { placeRepository.delete(place) } returns Unit

        service.delete(1L)

        verify { placeRepository.delete(place) }
    }

    @Test
    fun `존재하지 않는 id 로 조회하면 PLACE_NOT_FOUND 예외가 발생한다`() {
        every { placeRepository.findById(99L) } returns Optional.empty()

        assertThatThrownBy { service.getEntity(99L) }
            .isInstanceOf(CustomException::class.java)
            .matches { (it as CustomException).errorCode == ErrorCode.PLACE_NOT_FOUND }
    }
}

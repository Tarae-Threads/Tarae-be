package com.taraethreads.tarae.place.repository

import com.taraethreads.tarae.global.config.JpaConfig
import com.taraethreads.tarae.place.domain.Brand
import com.taraethreads.tarae.place.domain.BrandType
import com.taraethreads.tarae.place.domain.Category
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.domain.Tag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig::class)
class PlaceRepositoryTest {

    @Autowired
    lateinit var placeRepository: PlaceRepository

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var tagRepository: TagRepository

    @Autowired
    lateinit var brandRepository: BrandRepository

    private lateinit var categoryKnitShop: Category
    private lateinit var categoryWorkshop: Category
    private lateinit var tagParking: Tag
    private lateinit var brandSandnes: Brand

    @BeforeEach
    fun setUp() {
        categoryKnitShop = categoryRepository.save(Category(name = "뜨개샵"))
        categoryWorkshop = categoryRepository.save(Category(name = "공방"))
        tagParking = tagRepository.save(Tag(name = "주차가능"))
        brandSandnes = brandRepository.save(Brand(name = "산네스간", type = BrandType.YARN))
    }

    private fun createPlace(
        name: String = "테스트장소",
        region: String = "서울",
        district: String = "성수",
        categories: List<Category> = emptyList(),
        tags: List<Tag> = emptyList(),
    ): Place {
        val place = Place(name = name, region = region, district = district, address = "서울 성동구 테스트로 1")
        place.categories.addAll(categories)
        place.tags.addAll(tags)
        return placeRepository.save(place)
    }

    @Nested
    inner class `필터 없이 전체 조회` {
        @Test
        fun `모든 장소가 반환된다`() {
            // given
            createPlace(name = "장소A")
            createPlace(name = "장소B", region = "부산")

            // when
            val result = placeRepository.findAllWithFilters(null, null, null)

            // then
            assertThat(result).hasSize(2)
        }
    }

    @Nested
    inner class `지역 필터` {
        @Test
        fun `region으로 필터링하면 해당 지역 장소만 반환된다`() {
            // given
            createPlace(name = "서울장소", region = "서울")
            createPlace(name = "부산장소", region = "부산")

            // when
            val result = placeRepository.findAllWithFilters(region = "서울", categoryId = null, tagId = null)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("서울장소")
        }
    }

    @Nested
    inner class `카테고리 필터` {
        @Test
        fun `categoryId로 필터링하면 해당 카테고리 장소만 반환된다`() {
            // given
            createPlace(name = "뜨개샵장소", categories = listOf(categoryKnitShop))
            createPlace(name = "공방장소", categories = listOf(categoryWorkshop))

            // when
            val result = placeRepository.findAllWithFilters(null, categoryKnitShop.id, null)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("뜨개샵장소")
        }
    }

    @Nested
    inner class `태그 필터` {
        @Test
        fun `tagId로 필터링하면 해당 태그 장소만 반환된다`() {
            // given
            createPlace(name = "주차가능장소", tags = listOf(tagParking))
            createPlace(name = "주차불가장소")

            // when
            val result = placeRepository.findAllWithFilters(null, null, tagParking.id)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("주차가능장소")
        }
    }

    @Nested
    inner class `복합 필터` {
        @Test
        fun `region과 categoryId를 함께 사용하면 모두 만족하는 장소만 반환된다`() {
            // given
            createPlace(name = "서울뜨개샵", region = "서울", categories = listOf(categoryKnitShop))
            createPlace(name = "부산뜨개샵", region = "부산", categories = listOf(categoryKnitShop))
            createPlace(name = "서울공방", region = "서울", categories = listOf(categoryWorkshop))

            // when
            val result = placeRepository.findAllWithFilters("서울", categoryKnitShop.id, null)

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("서울뜨개샵")
        }
    }
}

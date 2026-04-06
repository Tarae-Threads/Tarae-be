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
        address: String = "서울 성동구 테스트로 1",
        description: String? = null,
        categories: List<Category> = emptyList(),
        tags: List<Tag> = emptyList(),
        brands: List<Brand> = emptyList(),
    ): Place {
        val place = Place(name = name, region = region, district = district, address = address, description = description)
        place.categories.addAll(categories)
        place.tags.addAll(tags)
        place.brands.addAll(brands)
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

    @Nested
    inner class `키워드 검색` {
        @Test
        fun `keyword가 없으면 전체 반환된다`() {
            // given
            createPlace(name = "장소A")
            createPlace(name = "장소B")

            // when
            val result = placeRepository.findAllWithFilters(null, null, null, keyword = null)

            // then
            assertThat(result).hasSize(2)
        }

        @Test
        fun `장소명으로 검색된다`() {
            // given
            createPlace(name = "실과바늘")
            createPlace(name = "공방마을")

            // when
            val result = placeRepository.findAllWithFilters(null, null, null, keyword = "실과")

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("실과바늘")
        }

        @Test
        fun `주소로 검색된다`() {
            // given
            createPlace(name = "장소A", address = "서울 성동구 뚝섬로 100")
            createPlace(name = "장소B", address = "부산 해운대구 해운대로 1")

            // when
            val result = placeRepository.findAllWithFilters(null, null, null, keyword = "뚝섬로")

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("장소A")
        }

        @Test
        fun `태그명으로 검색된다`() {
            // given
            createPlace(name = "주차가능장소", tags = listOf(tagParking))
            createPlace(name = "태그없는장소")

            // when
            val result = placeRepository.findAllWithFilters(null, null, null, keyword = "주차")

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("주차가능장소")
        }

        @Test
        fun `브랜드명으로 검색된다`() {
            // given
            createPlace(name = "산네스취급장소", brands = listOf(brandSandnes))
            createPlace(name = "브랜드없는장소")

            // when
            val result = placeRepository.findAllWithFilters(null, null, null, keyword = "산네스")

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("산네스취급장소")
        }

        @Test
        fun `공백으로 구분된 여러 토큰은 AND 조건으로 적용된다`() {
            // given
            createPlace(name = "서울실과바늘", region = "서울", address = "서울 성동구 실과로 1")
            createPlace(name = "부산실과바늘", region = "부산", address = "부산 해운대구 테스트로 1")

            // when
            val result = placeRepository.findAllWithFilters(null, null, null, keyword = "서울 실과")

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("서울실과바늘")
        }

        @Test
        fun `keyword와 region 필터를 함께 사용할 수 있다`() {
            // given
            createPlace(name = "서울뜨개샵", region = "서울")
            createPlace(name = "부산뜨개샵", region = "부산")

            // when
            val result = placeRepository.findAllWithFilters(region = "서울", null, null, keyword = "뜨개")

            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].name).isEqualTo("서울뜨개샵")
        }

        @Test
        fun `동일 장소가 여러 태그에 매칭되어도 중복 반환되지 않는다`() {
            // given
            val tag2 = tagRepository.save(Tag(name = "주차편리"))
            createPlace(name = "주차장소", tags = listOf(tagParking, tag2))

            // when
            val result = placeRepository.findAllWithFilters(null, null, null, keyword = "주차")

            // then
            assertThat(result).hasSize(1)
        }
    }
}

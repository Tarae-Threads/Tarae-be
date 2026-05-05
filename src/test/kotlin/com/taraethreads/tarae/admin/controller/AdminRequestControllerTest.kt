package com.taraethreads.tarae.admin.controller

import com.ninjasquad.springmockk.MockkBean
import com.taraethreads.tarae.admin.dto.AdminRequestListRow
import com.taraethreads.tarae.admin.service.AdminEventService
import com.taraethreads.tarae.admin.service.AdminRequestService
import com.taraethreads.tarae.admin.service.AdminShopRequestService
import com.taraethreads.tarae.event.domain.EventType
import com.taraethreads.tarae.place.dto.BrandResponse
import com.taraethreads.tarae.place.dto.CategoryResponse
import com.taraethreads.tarae.place.dto.TagResponse
import com.taraethreads.tarae.place.service.BrandService
import com.taraethreads.tarae.place.service.CategoryService
import com.taraethreads.tarae.place.service.TagService
import com.taraethreads.tarae.request.domain.EventRequest
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.request.domain.PlaceRequest
import com.taraethreads.tarae.request.domain.RequestStatus
import com.taraethreads.tarae.request.domain.RequestType
import com.taraethreads.tarae.request.domain.ShopRequest
import io.mockk.every
import io.mockk.justRun
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDate
import java.time.LocalDateTime

@WebMvcTest(
    controllers = [AdminRequestController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class],
)
class AdminRequestControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminRequestService: AdminRequestService

    @MockkBean
    lateinit var adminShopRequestService: AdminShopRequestService

    @MockkBean
    lateinit var adminEventService: AdminEventService

    @MockkBean
    lateinit var categoryService: CategoryService

    @MockkBean
    lateinit var tagService: TagService

    @MockkBean
    lateinit var brandService: BrandService

    @Nested
    inner class `GET 제보 목록` {

        private fun placeRow(name: String = "실과 바늘", requestType: RequestType = RequestType.NEW) =
            AdminRequestListRow(id = 1L, name = name, requestType = requestType, status = RequestStatus.PENDING, createdAt = LocalDateTime.of(2026, 4, 5, 0, 0))

        private fun shopRow(name: String = "뜨개마켓") =
            AdminRequestListRow(id = 2L, name = name, requestType = RequestType.NEW, status = RequestStatus.PENDING, createdAt = LocalDateTime.of(2026, 5, 1, 0, 0))

        @Test
        fun `type=place이면 장소 제보 목록을 반환한다`() {
            // given
            every { adminRequestService.getPlaceRequests(RequestStatus.PENDING, null) } returns listOf(placeRow())

            // when & then
            mockMvc.get("/admin/requests?type=place&status=PENDING").andExpect {
                status { isOk() }
                model { attribute("type", "place") }
            }
        }

        @Test
        fun `requestType=NEW이면 신규 제보만 반환한다`() {
            // given
            every { adminRequestService.getPlaceRequests(null, RequestType.NEW) } returns listOf(placeRow())

            // when & then
            mockMvc.get("/admin/requests?type=place&requestType=NEW").andExpect {
                status { isOk() }
                model { attribute("type", "place") }
                model { attribute("requestType", RequestType.NEW) }
            }
        }

        @Test
        fun `status와 requestType 모두 지정하면 교집합 목록을 반환한다`() {
            // given
            every { adminRequestService.getPlaceRequests(RequestStatus.PENDING, RequestType.UPDATE) } returns listOf(placeRow(requestType = RequestType.UPDATE))

            // when & then
            mockMvc.get("/admin/requests?type=place&status=PENDING&requestType=UPDATE").andExpect {
                status { isOk() }
                model { attribute("type", "place") }
                model { attribute("requestType", RequestType.UPDATE) }
            }
        }

        @Test
        fun `type=event이면 이벤트 제보 목록을 반환한다`() {
            // given
            every { adminRequestService.getEventRequests(null) } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests?type=event").andExpect {
                status { isOk() }
                model { attribute("type", "event") }
            }
        }

        @Test
        fun `type=shop이면 온라인샵 제보 목록을 반환한다`() {
            // given
            every { adminShopRequestService.getShopRequests(null, null) } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests?type=shop").andExpect {
                status { isOk() }
                model { attribute("type", "shop") }
            }
        }

        @Test
        fun `type=shop&status=PENDING이면 대기 중인 온라인샵 제보만 반환한다`() {
            // given
            every { adminShopRequestService.getShopRequests(RequestStatus.PENDING, null) } returns listOf(shopRow())

            // when & then
            mockMvc.get("/admin/requests?type=shop&status=PENDING").andExpect {
                status { isOk() }
                model { attribute("type", "shop") }
                model { attribute("status", RequestStatus.PENDING) }
            }
        }
    }

    @Nested
    inner class `GET 장소 제보 상세` {

        @Test
        fun `NEW 장소 제보 상세를 반환한다`() {
            // given
            every { adminRequestService.getPlaceRequest(1L) } returns
                PlaceRequest(requestType = RequestType.NEW, name = "실과 바늘")
            every { categoryService.getCategories() } returns listOf(CategoryResponse(id = 1L, name = "뜨개샵"))
            every { tagService.getAll() } returns listOf(TagResponse(id = 1L, name = "주차가능"))
            every { brandService.getAll() } returns listOf(BrandResponse(id = 1L, name = "산네스간", type = "YARN"))
            every { brandService.getBrandsGroupedByType() } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests/place/1").andExpect {
                status { isOk() }
                model { attributeExists("request") }
                model { attributeExists("form") }
                model { attributeExists("categories") }
            }
        }

        @Test
        fun `NEW 장소 제보 상세에서 선택된 카테고리와 브랜드 이름이 모델에 포함된다`() {
            // given
            every { adminRequestService.getPlaceRequest(1L) } returns
                PlaceRequest(
                    requestType = RequestType.NEW,
                    name = "실과 바늘",
                    categoryIds = listOf(1L, 2L),
                    brandYarnIds = listOf(10L),
                    brandNeedleIds = listOf(20L),
                    brandNotionsIds = listOf(30L),
                    brandPatternbookIds = listOf(40L),
                )
            every { categoryService.getCategories() } returns listOf(
                CategoryResponse(id = 1L, name = "뜨개샵"),
                CategoryResponse(id = 2L, name = "부자재"),
                CategoryResponse(id = 99L, name = "기타"),
            )
            every { tagService.getAll() } returns emptyList()
            every { brandService.getAll() } returns listOf(
                BrandResponse(id = 10L, name = "산네스간", type = "YARN"),
                BrandResponse(id = 20L, name = "치아오궁", type = "NEEDLE"),
                BrandResponse(id = 30L, name = "클로버", type = "NOTIONS"),
                BrandResponse(id = 40L, name = "로완", type = "PATTERNBOOK"),
            )
            every { brandService.getBrandsGroupedByType() } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests/place/1").andExpect {
                status { isOk() }
                model { attribute("selectedCategoryNames", listOf("뜨개샵", "부자재")) }
                model { attribute("selectedBrandYarnNames", listOf("산네스간")) }
                model { attribute("selectedBrandNeedleNames", listOf("치아오궁")) }
                model { attribute("selectedBrandNotionsNames", listOf("클로버")) }
                model { attribute("selectedBrandPatternbookNames", listOf("로완")) }
            }
        }

        @Test
        fun `제보에 존재하지 않는 카테고리 ID가 포함되면 해당 이름은 빈 리스트로 resolve된다`() {
            // given
            every { adminRequestService.getPlaceRequest(1L) } returns
                PlaceRequest(
                    requestType = RequestType.NEW,
                    name = "실과 바늘",
                    categoryIds = listOf(999L),
                    brandYarnIds = listOf(888L),
                )
            every { categoryService.getCategories() } returns listOf(
                CategoryResponse(id = 1L, name = "뜨개샵"),
            )
            every { tagService.getAll() } returns emptyList()
            every { brandService.getAll() } returns listOf(
                BrandResponse(id = 10L, name = "산네스간", type = "YARN"),
            )
            every { brandService.getBrandsGroupedByType() } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests/place/1").andExpect {
                status { isOk() }
                model { attribute("selectedCategoryNames", emptyList<String>()) }
                model { attribute("selectedBrandYarnNames", emptyList<String>()) }
            }
        }

        @Test
        fun `UPDATE 장소 제보 상세에서는 기존 Place 데이터가 폼에 채워진다`() {
            // given
            val existingPlace = Place(
                name = "실과 바늘",
                region = "서울",
                district = "성수",
                address = "서울 성동구 성수이로 1",
                hoursText = "10:00-18:00",
                closedDays = "일요일",
                description = "뜨개 편집샵",
                instagramUrl = "https://instagram.com/test",
            )
            every { adminRequestService.getPlaceRequest(2L) } returns
                PlaceRequest(requestType = RequestType.UPDATE, placeId = 10L, name = "실과 바늘 수정요청")
            every { adminRequestService.getPlace(10L) } returns existingPlace
            every { categoryService.getCategories() } returns emptyList()
            every { tagService.getAll() } returns emptyList()
            every { brandService.getAll() } returns emptyList()
            every { brandService.getBrandsGroupedByType() } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests/place/2").andExpect {
                status { isOk() }
                model { attributeExists("request") }
                model { attributeExists("form") }
                model { attributeExists("existingPlace") }
            }
        }
    }

    @Nested
    inner class `POST 장소 제보 승인` {

        @Test
        fun `승인 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.approvePlaceRequest(1L, any()) }

            // when & then
            mockMvc.post("/admin/requests/place/1/approve") {
                param("name", "실과 바늘")
                param("region", "서울")
                param("district", "성수")
                param("address", "서울 성동구")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=place&status=PENDING")
            }
        }
    }

    @Nested
    inner class `POST 장소 제보 거절` {

        @Test
        fun `거절 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.rejectPlaceRequest(1L) }

            // when & then
            mockMvc.post("/admin/requests/place/1/reject").andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=place&status=PENDING")
            }
        }
    }

    @Nested
    inner class `GET 이벤트 제보 상세` {

        @Test
        fun `이벤트 제보 상세를 반환한다`() {
            // given
            every { adminRequestService.getEventRequest(1L) } returns EventRequest(
                title = "뜨개 팝업",
                eventType = EventType.EVENT_POPUP,
                startDate = LocalDate.of(2026, 5, 1),
            )
            every { adminEventService.placeOptions() } returns emptyList()

            // when & then
            mockMvc.get("/admin/requests/event/1").andExpect {
                status { isOk() }
                model { attributeExists("request") }
                model { attributeExists("form") }
                model { attributeExists("eventTypes") }
                model { attributeExists("places") }
            }
        }
    }

    @Nested
    inner class `POST 이벤트 제보 승인` {

        @Test
        fun `승인 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.approveEventRequest(1L, any()) }

            // when & then
            mockMvc.post("/admin/requests/event/1/approve") {
                param("title", "뜨개 팝업")
                param("eventType", "EVENT_POPUP")
                param("startDate", "2026-05-01")
            }.andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=event&status=PENDING")
            }
        }
    }

    @Nested
    inner class `POST 이벤트 제보 거절` {

        @Test
        fun `거절 후 PENDING 목록으로 리다이렉트한다`() {
            // given
            justRun { adminRequestService.rejectEventRequest(1L) }

            // when & then
            mockMvc.post("/admin/requests/event/1/reject").andExpect {
                status { is3xxRedirection() }
                redirectedUrl("/admin/requests?type=event&status=PENDING")
            }
        }
    }
}

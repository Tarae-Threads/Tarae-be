package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.AdminPlaceListRow
import com.taraethreads.tarae.admin.dto.PlaceCreateForm
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Place
import com.taraethreads.tarae.place.repository.PlaceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminPlaceService(
    private val placeRepository: PlaceRepository,
    private val placeAssociationSyncer: PlaceAssociationSyncer,
) {

    fun list(keyword: String?, pageable: Pageable): Page<AdminPlaceListRow> =
        placeRepository.findAllForAdmin(keyword, pageable).map { AdminPlaceListRow.from(it) }

    fun getEntity(id: Long): Place =
        placeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PLACE_NOT_FOUND) }

    fun getForm(id: Long): PlaceCreateForm {
        val place = getEntity(id)
        return PlaceCreateForm(
            name = place.name,
            status = place.status,
            region = place.region,
            district = place.district,
            address = place.address,
            lat = place.lat,
            lng = place.lng,
            hoursText = place.hoursText,
            closedDays = place.closedDays,
            description = place.description,
            instagramUrl = place.instagramUrl,
            websiteUrl = place.websiteUrl,
            naverMapUrl = place.naverMapUrl,
            categoryIds = place.categories.map { it.id },
            tagIds = place.tags.map { it.id },
            brandIds = place.brands.map { it.id },
        )
    }

    @Transactional
    fun createBulk(forms: List<PlaceCreateForm>): List<Long> {
        return forms.map { form ->
            val place = Place(
                name = form.name,
                status = form.status,
                region = form.region,
                district = form.district,
                address = form.address,
                lat = form.lat,
                lng = form.lng,
                hoursText = form.hoursText,
                closedDays = form.closedDays,
                description = form.description,
                instagramUrl = form.instagramUrl,
                websiteUrl = form.websiteUrl,
                naverMapUrl = form.naverMapUrl,
            )
            placeAssociationSyncer.attach(place, form)
            placeRepository.save(place).id
        }
    }

    @Transactional
    fun update(id: Long, form: PlaceCreateForm) {
        val place = getEntity(id)
        place.update(form)
        placeAssociationSyncer.sync(place, form)
    }

    @Transactional
    fun toggleActive(id: Long) {
        val place = getEntity(id)
        if (place.active) place.deactivate() else place.activate()
    }

    @Transactional
    fun delete(id: Long) {
        val place = getEntity(id)
        placeRepository.delete(place)
    }

}

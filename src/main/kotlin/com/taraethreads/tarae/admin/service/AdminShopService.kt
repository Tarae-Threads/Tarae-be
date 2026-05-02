package com.taraethreads.tarae.admin.service

import com.taraethreads.tarae.admin.dto.AdminShopListRow
import com.taraethreads.tarae.admin.dto.ShopCreateForm
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.shop.domain.Shop
import com.taraethreads.tarae.shop.repository.ShopRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AdminShopService(
    private val shopRepository: ShopRepository,
    private val shopAssociationSyncer: ShopAssociationSyncer,
) {

    fun list(keyword: String?, pageable: Pageable): Page<AdminShopListRow> =
        shopRepository.findAllForAdmin(keyword, pageable).map { AdminShopListRow.from(it) }

    fun getEntity(id: Long): Shop =
        shopRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.SHOP_NOT_FOUND) }

    fun getForm(id: Long): ShopCreateForm {
        val shop = getEntity(id)
        return ShopCreateForm(
            name = shop.name,
            instagramUrl = shop.instagramUrl,
            naverUrl = shop.naverUrl,
            websiteUrl = shop.websiteUrl,
            categoryIds = shop.categories.map { it.id },
            tagIds = shop.tags.map { it.id },
            brandIds = shop.brands.map { it.id },
        )
    }

    @Transactional
    fun createBulk(forms: List<ShopCreateForm>): List<Long> =
        forms.map { form ->
            val shop = Shop(
                name = form.name,
                instagramUrl = form.instagramUrl,
                naverUrl = form.naverUrl,
                websiteUrl = form.websiteUrl,
            )
            shopAssociationSyncer.attach(shop, form)
            shopRepository.save(shop).id
        }

    @Transactional
    fun update(id: Long, form: ShopCreateForm) {
        val shop = getEntity(id)
        shop.update(form)
        shopAssociationSyncer.sync(shop, form)
    }

    @Transactional
    fun toggleActive(id: Long) {
        val shop = getEntity(id)
        if (shop.active) shop.deactivate() else shop.activate()
    }

    @Transactional
    fun delete(id: Long) {
        val shop = getEntity(id)
        shopRepository.delete(shop)
    }
}

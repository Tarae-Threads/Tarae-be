package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.admin.dto.AdminTagRow
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Tag
import com.taraethreads.tarae.place.dto.TagResponse
import com.taraethreads.tarae.place.repository.TagRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TagService(
    private val tagRepository: TagRepository,
) {

    fun getAll(): List<TagResponse> = tagRepository.findAll().map { TagResponse.from(it) }

    fun getAllForAdmin(): List<AdminTagRow> {
        val usageMap = tagRepository.countPlaceUsagesGrouped()
            .associate { (it[0] as Long) to (it[1] as Long) }
        return tagRepository.findAllByOrderById()
            .map { AdminTagRow.from(it, usageMap[it.id] ?: 0) }
    }

    @Transactional
    fun create(name: String): Tag {
        val trimmed = name.trim()
        if (tagRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        return tagRepository.save(Tag(name = trimmed))
    }

    @Transactional
    fun rename(id: Long, name: String) {
        val tag = tagRepository.findByIdOrNull(id)
            ?: throw CustomException(ErrorCode.TAG_NOT_FOUND)
        val trimmed = name.trim()
        if (trimmed != tag.name && tagRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        tag.rename(trimmed)
    }

    fun countPlaceUsages(id: Long): Long = tagRepository.countPlaceUsages(id)

    @Transactional
    fun delete(id: Long) {
        val tag = tagRepository.findByIdOrNull(id)
            ?: throw CustomException(ErrorCode.TAG_NOT_FOUND)
        tagRepository.deletePlaceMappings(id)
        tagRepository.delete(tag)
    }
}

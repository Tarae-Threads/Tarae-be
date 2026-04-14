package com.taraethreads.tarae.place.service

import com.taraethreads.tarae.admin.dto.AdminTagRow
import com.taraethreads.tarae.global.exception.CustomException
import com.taraethreads.tarae.global.exception.ErrorCode
import com.taraethreads.tarae.place.domain.Tag
import com.taraethreads.tarae.place.dto.TagResponse
import com.taraethreads.tarae.place.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TagService(
    private val tagRepository: TagRepository,
) {

    fun getAll(): List<TagResponse> = tagRepository.findAll().map { TagResponse.from(it) }

    fun getAllForAdmin(): List<AdminTagRow> =
        tagRepository.findAll()
            .sortedBy { it.id }
            .map { AdminTagRow.from(it) }

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
        val tag = tagRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.TAG_NOT_FOUND) }
        val trimmed = name.trim()
        if (trimmed != tag.name && tagRepository.existsByName(trimmed)) {
            throw CustomException(ErrorCode.DUPLICATE_MASTER_NAME)
        }
        tag.rename(trimmed)
    }
}

package com.taraethreads.tarae.place.service

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
}

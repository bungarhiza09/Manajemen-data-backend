package org.delcom.service

import org.delcom.model.Guru
import org.delcom.repository.GuruRepository

class GuruService {

    private val repo = GuruRepository()

    fun getAll() = repo.getAll()

    fun getById(id: Int) = repo.getById(id)

    fun create(guru: Guru) = repo.create(guru)

    fun update(id: Int, guru: Guru) = repo.update(id, guru)

    fun delete(id: Int) = repo.delete(id)

}
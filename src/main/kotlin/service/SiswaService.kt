package org.delcom.service

import org.delcom.model.Siswa
import org.delcom.repository.SiswaRepository

class SiswaService {

    private val repo = SiswaRepository()

    fun getAll() = repo.getAll()

    fun getById(id: Int) = repo.getById(id)

    fun create(siswa: Siswa) = repo.create(siswa)

    fun update(id: Int, siswa: Siswa) = repo.update(id, siswa)

    fun delete(id: Int) = repo.delete(id)

    fun getRaporFile(id: Int) = repo.getRaporFile(id)

    fun getSklFile(id: Int) = repo.getSklFile(id)

    fun getIjazahFile(id: Int) = repo.getIjazahFile(id)
}
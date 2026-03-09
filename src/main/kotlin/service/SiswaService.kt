package service

import model.Siswa
import repository.SiswaRepository

class SiswaService {

    private val repo = SiswaRepository()

    fun getAll() = repo.getAll()

    fun getById(id: Int) = repo.getById(id)

    fun create(siswa: Siswa) = repo.create(siswa)

    fun update(id: Int, siswa: Siswa) = repo.update(id, siswa)

    fun delete(id: Int) = repo.delete(id)

}
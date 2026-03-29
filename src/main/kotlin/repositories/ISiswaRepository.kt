package org.delcom.repositories

import org.delcom.entities.Siswa
import org.delcom.helpers.suspendTransaction

interface ISiswaRepository {

    suspend fun getAll(limit: Int, offset: Long): List<Siswa>

    suspend fun getById(id: String): Siswa?

    suspend fun create(siswa: Siswa): Siswa

    suspend fun update(
        id: String,
        namaLengkap: String,
        jurusan: String,
        nisn: String,
        nis: String,
        kelas: String,
        tanggalLahir: String,
        alamat: String,
        noWaOrtu: String,
        status: String,
        raporFile: String?,
        sklFile: String?,
        ijazahFile: String?
    ): Boolean

    suspend fun delete(id: String): Boolean

    suspend fun search(
        keyword: String?,
        sortBy: String?,
        order: String?,
        limit: Int,
        offset: Long
    ): List<Siswa>

    suspend fun getStats(): Map<String, Int>
}
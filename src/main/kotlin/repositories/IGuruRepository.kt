package org.delcom.repositories

import org.delcom.entities.Guru

interface IGuruRepository {

    suspend fun getAll(limit: Int, offset: Long): List<Guru>

    suspend fun getById(id: String): Guru?

    suspend fun create(guru: Guru): Guru

    suspend fun update(
        id: String,
        namaLengkap: String,
        nip: String,
        noTelepon: String,
        anakWali: String,
        mataPelajaran: String,
        alamat: String
    ): Boolean

    suspend fun delete(id: String): Boolean

    suspend fun search(
        keyword: String?,
        sortBy: String?,
        order: String?,
        limit: Int,
        offset: Long
    ): List<Guru>
}
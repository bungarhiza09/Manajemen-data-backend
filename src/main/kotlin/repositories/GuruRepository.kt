package org.delcom.repositories

import org.delcom.dao.GuruDAO
import org.delcom.entities.Guru
import org.delcom.helpers.guruDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.GuruTable
import org.jetbrains.exposed.sql.or

class GuruRepository : IGuruRepository {

    override suspend fun getAll(limit: Int, offset: Long): List<Guru> = suspendTransaction {
        GuruDAO.all()
            .limit(limit)
            .offset(offset)
            .map { guruDAOToModel(it) }
    }

    override suspend fun getById(id: String): Guru? = suspendTransaction {
        GuruDAO.findById(id.toInt())
            ?.let { guruDAOToModel(it) }
    }

    override suspend fun create(guru: Guru): Guru = suspendTransaction {
        val dao = GuruDAO.new {
            namaLengkap = guru.namaLengkap
            nip = guru.nip
            noTelepon = guru.noTelepon
            anakWali = guru.anakWali
            mataPelajaran = guru.mataPelajaran
            alamat = guru.alamat
        }
        guruDAOToModel(dao)
    }

    override suspend fun update(
        id: String,
        namaLengkap: String,
        nip: String,
        noTelepon: String,
        anakWali: String,
        mataPelajaran: String,
        alamat: String
    ): Boolean = suspendTransaction {

        val guru = GuruDAO.findById(id.toInt())
            ?: return@suspendTransaction false

        guru.namaLengkap = namaLengkap
        guru.nip = nip
        guru.noTelepon = noTelepon
        guru.anakWali = anakWali
        guru.mataPelajaran = mataPelajaran
        guru.alamat = alamat

        true
    }

    override suspend fun delete(id: String): Boolean = suspendTransaction {
        val guru = GuruDAO.findById(id.toInt())
            ?: return@suspendTransaction false

        guru.delete()
        true
    }

    // 🔍 SEARCH + SORT DIPISAH PARAMETER
    override suspend fun search(
        keyword: String?,
        sortBy: String?,
        order: String?,
        limit: Int,
        offset: Long
    ): List<Guru> = suspendTransaction {

        // 🔎 SEARCH
        val baseQuery = if (!keyword.isNullOrEmpty()) {
            GuruDAO.find {
                (GuruTable.nama_lengkap like "%$keyword%") or
                        (GuruTable.mata_pelajaran like "%$keyword%")
            }
        } else {
            GuruDAO.all()
        }

        // 🔄 SORT
        val sorted = when (sortBy) {

            "nama" -> if (order == "asc")
                baseQuery.sortedBy { it.namaLengkap }
            else
                baseQuery.sortedByDescending { it.namaLengkap }

            "nip" -> if (order == "asc")
                baseQuery.sortedBy { it.nip }
            else
                baseQuery.sortedByDescending { it.nip }

            "mapel" -> if (order == "asc")
                baseQuery.sortedBy { it.mataPelajaran }
            else
                baseQuery.sortedByDescending { it.mataPelajaran }

            else -> baseQuery.sortedByDescending { it.id.value } // default terbaru
        }

        // 📄 PAGINATION
        sorted
            .drop(offset.toInt())
            .take(limit)
            .map { guruDAOToModel(it) }
    }
}
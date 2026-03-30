package org.delcom.repositories

import org.delcom.dao.SiswaDAO
import org.delcom.entities.Siswa
import org.delcom.helpers.siswaDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.SiswaTable
import org.jetbrains.exposed.sql.or

class SiswaRepository : ISiswaRepository {

    override suspend fun getAll(limit: Int, offset: Long): List<Siswa> = suspendTransaction {
        SiswaDAO.all()
            .limit(limit)
            .offset(offset)
            .map { siswaDAOToModel(it) }
    }

    override suspend fun getById(id: String): Siswa? = suspendTransaction {
        SiswaDAO.findById(id.toInt())
            ?.let { siswaDAOToModel(it) }
    }

    override suspend fun create(siswa: Siswa): Siswa = suspendTransaction {
        val dao = SiswaDAO.new {
            namaLengkap = siswa.namaLengkap
            jurusan = siswa.jurusan
            nisn = siswa.nisn
            nis = siswa.nis
            kelas = siswa.kelas
            tanggalLahir = siswa.tanggalLahir
            alamat = siswa.alamat
            noWaOrtu = siswa.noWaOrtu
            status = siswa.status
            raporFile = siswa.raporFile
            sklFile = siswa.sklFile
            ijazahFile = siswa.ijazahFile
        }
        siswaDAOToModel(dao)
    }

    override suspend fun update(
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
    ): Boolean = suspendTransaction {

        val siswa = SiswaDAO.findById(id.toInt())
            ?: return@suspendTransaction false

        siswa.namaLengkap = namaLengkap
        siswa.jurusan = jurusan
        siswa.nisn = nisn
        siswa.nis = nis
        siswa.kelas = kelas
        siswa.tanggalLahir = tanggalLahir
        siswa.alamat = alamat
        siswa.noWaOrtu = noWaOrtu
        siswa.status = status
        siswa.raporFile = raporFile
        siswa.sklFile = sklFile
        siswa.ijazahFile = ijazahFile

        true
    }

    override suspend fun delete(id: String): Boolean = suspendTransaction {
        val siswa = SiswaDAO.findById(id.toInt())
            ?: return@suspendTransaction false

        siswa.delete()
        true
    }

    override suspend fun search(
        keyword: String?,
        sortBy: String?,
        order: String?,
        limit: Int,
        offset: Long
    ): List<Siswa> = suspendTransaction {

        val baseQuery = if (!keyword.isNullOrEmpty()) {
            SiswaDAO.find {
                (SiswaTable.nama_lengkap like "%$keyword%") or
                        (SiswaTable.nisn like "%$keyword%")
            }
        } else {
            SiswaDAO.all()
        }

        val sorted = when (sortBy) {

            "nama" -> if (order == "asc")
                baseQuery.sortedBy { it.namaLengkap }
            else
                baseQuery.sortedByDescending { it.namaLengkap }

            "nisn" -> if (order == "asc")
                baseQuery.sortedBy { it.nisn }
            else
                baseQuery.sortedByDescending { it.nisn }

            "kelas" -> if (order == "asc")
                baseQuery.sortedBy { it.kelas }
            else
                baseQuery.sortedByDescending { it.kelas }

            else -> baseQuery.sortedByDescending { it.id.value }
        }

        sorted
            .drop(offset.toInt())
            .take(limit)
            .map { siswaDAOToModel(it) }
    }

    override suspend fun getStats(): Map<String, Int> = suspendTransaction {

        val total = SiswaDAO.all().count().toInt()

        val aktif = SiswaDAO.find {
            SiswaTable.status eq "aktif"
        }.count().toInt()

        val lulus = SiswaDAO.find {
            SiswaTable.status eq "lulus"
        }.count().toInt()

        mapOf(
            "total" to total,
            "aktif" to aktif,
            "lulus" to lulus
        )
    }

    override suspend fun getAllForExport(): List<SiswaDAO> = suspendTransaction {
        SiswaDAO.all().toList()
    }

    override suspend fun updateRaporFile(id: String, path: String): Boolean = suspendTransaction {
        val siswa = SiswaDAO.findById(id.toInt()) ?: return@suspendTransaction false
        siswa.raporFile = path
        true
    }

    override suspend fun updateSklFile(id: String, path: String): Boolean = suspendTransaction {
        val siswa = SiswaDAO.findById(id.toInt()) ?: return@suspendTransaction false
        siswa.sklFile = path
        true
    }

    override suspend fun updateIjazahFile(id: String, path: String): Boolean = suspendTransaction {
        val siswa = SiswaDAO.findById(id.toInt()) ?: return@suspendTransaction false
        siswa.ijazahFile = path
        true
    }

}
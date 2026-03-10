package org.delcom.repository

import org.delcom.database.SiswaTable
import org.delcom.model.Siswa
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.deleteWhere

class SiswaRepository {

    fun getAll(): List<Siswa> = transaction {
        SiswaTable.selectAll().map {
            Siswa(
                id = it[SiswaTable.id].value,
                nama_lengkap = it[SiswaTable.nama_lengkap],
                jurusan = it[SiswaTable.jurusan],
                nisn = it[SiswaTable.nisn],
                nis = it[SiswaTable.nis],
                kelas = it[SiswaTable.kelas],
                tanggal_lahir = it[SiswaTable.tanggal_lahir],
                alamat = it[SiswaTable.alamat],
                no_wa_ortu = it[SiswaTable.no_wa_ortu]
            )
        }
    }

    fun getById(id: Int): Siswa? = transaction {

        SiswaTable
            .selectAll()
            .where { SiswaTable.id eq id }
            .map {
                Siswa(
                    id = it[SiswaTable.id].value,
                    nama_lengkap = it[SiswaTable.nama_lengkap],
                    jurusan = it[SiswaTable.jurusan],
                    nisn = it[SiswaTable.nisn],
                    nis = it[SiswaTable.nis],
                    kelas = it[SiswaTable.kelas],
                    tanggal_lahir = it[SiswaTable.tanggal_lahir],
                    alamat = it[SiswaTable.alamat],
                    no_wa_ortu = it[SiswaTable.no_wa_ortu]
                )
            }
            .singleOrNull()

    }

    fun create(siswa: Siswa) = transaction {
        SiswaTable.insert {
            it[nama_lengkap] = siswa.nama_lengkap
            it[jurusan] = siswa.jurusan
            it[nisn] = siswa.nisn
            it[nis] = siswa.nis
            it[kelas] = siswa.kelas
            it[tanggal_lahir] = siswa.tanggal_lahir
            it[alamat] = siswa.alamat
            it[no_wa_ortu] = siswa.no_wa_ortu
        }
    }

    fun update(id: Int, siswa: Siswa) = transaction {
        SiswaTable.update({ SiswaTable.id eq id }) {
            it[nama_lengkap] = siswa.nama_lengkap
            it[jurusan] = siswa.jurusan
            it[nisn] = siswa.nisn
            it[nis] = siswa.nis
            it[kelas] = siswa.kelas
            it[tanggal_lahir] = siswa.tanggal_lahir
            it[alamat] = siswa.alamat
            it[no_wa_ortu] = siswa.no_wa_ortu
        }
    }

    fun delete(id: Int) = transaction {
        SiswaTable.deleteWhere {
            SiswaTable.id eq id
        }
    }

    fun getRaporFile(id: Int): String? = transaction {
        SiswaTable
            .selectAll()
            .where { SiswaTable.id eq id }
            .map { it[SiswaTable.rapor_file] }
            .singleOrNull()
    }

    fun getSklFile(id: Int): String? = transaction {
        SiswaTable
            .selectAll()
            .where { SiswaTable.id eq id }
            .map { it[SiswaTable.skl_file] }
            .singleOrNull()
    }

    fun getIjazahFile(id: Int): String? = transaction {

        SiswaTable
            .selectAll()
            .where { SiswaTable.id eq id }
            .map { it[SiswaTable.ijazah_file] }
            .singleOrNull()

    }
}
package org.delcom.repository

import org.delcom.database.GuruTable
import org.delcom.model.Guru
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class GuruRepository {

    fun getAll(): List<Guru> = transaction {
        GuruTable.selectAll().map {
            Guru(
                id = it[GuruTable.id].value,
                nama_lengkap = it[GuruTable.nama_lengkap],
                nip = it[GuruTable.nip],
                no_telepon = it[GuruTable.no_telepon],
                anak_wali = it[GuruTable.anak_wali],
                mata_pelajaran = it[GuruTable.mata_pelajaran],
                alamat = it[GuruTable.alamat]
            )
        }
    }

    fun getById(id: Int): Guru? = transaction {

        GuruTable
            .selectAll()
            .where { GuruTable.id eq id }
            .map {
                Guru(
                    id = it[GuruTable.id].value,
                    nama_lengkap = it[GuruTable.nama_lengkap],
                    nip = it[GuruTable.nip],
                    no_telepon = it[GuruTable.no_telepon],
                    anak_wali = it[GuruTable.anak_wali],
                    mata_pelajaran = it[GuruTable.mata_pelajaran],
                    alamat = it[GuruTable.alamat]
                )
            }
            .singleOrNull()

    }

    fun create(guru: Guru) = transaction {
        GuruTable.insert {
            it[nama_lengkap] = guru.nama_lengkap
            it[nip] = guru.nip
            it[no_telepon] = guru.no_telepon
            it[anak_wali] = guru.anak_wali
            it[mata_pelajaran] = guru.mata_pelajaran
            it[alamat] = guru.alamat
        }
    }

    fun update(id: Int, guru: Guru) = transaction {
        GuruTable.update({ GuruTable.id eq id }) {
            it[nama_lengkap] = guru.nama_lengkap
            it[nip] = guru.nip
            it[no_telepon] = guru.no_telepon
            it[anak_wali] = guru.anak_wali
            it[mata_pelajaran] = guru.mata_pelajaran
            it[alamat] = guru.alamat
        }
    }

    fun delete(id: Int) = transaction {
        GuruTable.deleteWhere { GuruTable.id eq id }
    }
}
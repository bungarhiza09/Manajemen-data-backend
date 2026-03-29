package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.ZoneOffset
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.Clock

import org.delcom.dao.GuruDAO
import org.delcom.dao.SiswaDAO
import org.delcom.entities.Guru
import org.delcom.entities.Siswa


suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun siswaDAOToModel(dao: SiswaDAO) = Siswa(
    id = dao.id.value.toString(),
    namaLengkap = dao.namaLengkap,
    jurusan = dao.jurusan,
    nisn = dao.nisn,
    nis = dao.nis,
    kelas = dao.kelas,
    tanggalLahir = dao.tanggalLahir,
    alamat = dao.alamat,
    noWaOrtu = dao.noWaOrtu,
    status = dao.status,

    raporFile = dao.raporFile,
    sklFile = dao.sklFile,
    ijazahFile = dao.ijazahFile
)

fun guruDAOToModel(dao: GuruDAO) = Guru(
    id = dao.id.value.toString(),
    namaLengkap = dao.namaLengkap,
    nip = dao.nip,
    noTelepon = dao.noTelepon,
    anakWali = dao.anakWali,
    mataPelajaran = dao.mataPelajaran,
    alamat = dao.alamat
)
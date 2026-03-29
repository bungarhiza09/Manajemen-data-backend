package org.delcom.entities

import kotlinx.serialization.Serializable

@Serializable
data class Siswa(
    val id: String,
    val namaLengkap: String,
    val jurusan: String,
    val nisn: String,
    val nis: String,
    val kelas: String,
    val tanggalLahir: String,
    val alamat: String,
    val noWaOrtu: String,
    val raporFile: String?,
    val sklFile: String?,
    val ijazahFile: String?
)
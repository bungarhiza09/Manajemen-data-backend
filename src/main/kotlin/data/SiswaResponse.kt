package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class SiswaResponse(
    val id: String,
    val namaLengkap: String,
    val jurusan: String,
    val nisn: String,
    val nis: String,
    val kelas: String,
    val tanggalLahir: String,
    val alamat: String,
    val noWaOrtu: String,
    val status: String,

    // dokumen
    val raporFile: String?,
    val sklFile: String?,
    val ijazahFile: String?
)
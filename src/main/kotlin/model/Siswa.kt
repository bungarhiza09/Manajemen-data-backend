package org.delcom.model

import kotlinx.serialization.Serializable

@Serializable
data class Siswa(
    val id: Int? = null,
    val nama_lengkap: String,
    val jurusan: String,
    val nisn: String,
    val nis: String,
    val kelas: String,
    val tanggal_lahir: String,
    val alamat: String,
    val no_wa_ortu: String,

    val rapor_file: String? = null,
    val skl_file: String? = null,
    val ijazah_file: String? = null
)
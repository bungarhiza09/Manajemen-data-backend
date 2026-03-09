package model

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
    val no_wa_ortu: String
)
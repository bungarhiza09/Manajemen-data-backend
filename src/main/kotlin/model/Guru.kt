package org.delcom.model

import kotlinx.serialization.Serializable

@Serializable
data class Guru(
    val id: Int? = null,
    val nama_lengkap: String,
    val nip: String,
    val no_telepon: String,
    val anak_wali: String,
    val mata_pelajaran: String,
    val alamat: String
)
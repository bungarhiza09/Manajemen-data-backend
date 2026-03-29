package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class GuruResponse(
    val id: String,
    val namaLengkap: String,
    val nip: String,
    val noTelepon: String,
    val anakWali: String,
    val mataPelajaran: String,
    val alamat: String
)
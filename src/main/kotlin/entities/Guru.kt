package org.delcom.entities

import kotlinx.serialization.Serializable

@Serializable
data class Guru(
    val id: String,
    val namaLengkap: String,
    val nip: String,
    val noTelepon: String,
    val anakWali: String,
    val mataPelajaran: String,
    val alamat: String,
    val jabatan: String,
)
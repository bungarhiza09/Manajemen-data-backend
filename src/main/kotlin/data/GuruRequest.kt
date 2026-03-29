package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Guru

@Serializable
data class GuruRequest(
    var namaLengkap: String = "",
    var nip: String = "",
    var noTelepon: String = "",
    var anakWali: String = "",
    var mataPelajaran: String = "",
    var alamat: String = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "namaLengkap" to namaLengkap,
            "nip" to nip,
            "noTelepon" to noTelepon,
            "anakWali" to anakWali,
            "mataPelajaran" to mataPelajaran,
            "alamat" to alamat
        )
    }

    fun toEntity(): Guru {
        return Guru(
            id = "", // diisi DB
            namaLengkap = namaLengkap,
            nip = nip,
            noTelepon = noTelepon,
            anakWali = anakWali,
            mataPelajaran = mataPelajaran,
            alamat = alamat
        ) 
    }
}
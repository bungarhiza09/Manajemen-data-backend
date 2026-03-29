package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Siswa

@Serializable
data class SiswaRequest(
    var namaLengkap: String = "",
    var jurusan: String = "",
    var nisn: String = "",
    var nis: String = "",
    var kelas: String = "",
    var tanggalLahir: String = "",
    var alamat: String = "",
    var noWaOrtu: String = "",
    var status: String = "",
    var raporFile: String? = null,
    var sklFile: String? = null,
    var ijazahFile: String? = null
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "namaLengkap" to namaLengkap,
            "jurusan" to jurusan,
            "nisn" to nisn,
            "nis" to nis,
            "kelas" to kelas,
            "tanggalLahir" to tanggalLahir,
            "alamat" to alamat,
            "noWaOrtu" to noWaOrtu,
            "status" to status,
            "raporFile" to raporFile,
            "sklFile" to sklFile,
            "ijazahFile" to ijazahFile
        )
    }

    fun toEntity(): Siswa {
        return Siswa(
            id = "", // biasanya diisi oleh database
            namaLengkap = namaLengkap,
            jurusan = jurusan,
            nisn = nisn,
            nis = nis,
            kelas = kelas,
            tanggalLahir = tanggalLahir,
            alamat = alamat,
            noWaOrtu = noWaOrtu,
            status = status,
            raporFile = raporFile,
            sklFile = sklFile,
            ijazahFile = ijazahFile
        )
    }
}
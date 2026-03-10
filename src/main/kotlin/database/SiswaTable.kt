package org.delcom.database

import org.jetbrains.exposed.dao.id.IntIdTable

object SiswaTable : IntIdTable("siswa") {

    val nama_lengkap = varchar("nama_lengkap", 255)
    val jurusan = varchar("jurusan", 100)
    val nisn = varchar("nisn", 50)
    val nis = varchar("nis", 50)
    val kelas = varchar("kelas", 20)
    val tanggal_lahir = varchar("tanggal_lahir", 20)
    val alamat = text("alamat")
    val no_wa_ortu = varchar("no_wa_ortu", 20)

    // dokumen siswa
    val rapor_file = varchar("rapor_file", 255).nullable()
    val skl_file = varchar("skl_file", 255).nullable()
    val ijazah_file = varchar("ijazah_file", 255).nullable()
}
package database

import org.jetbrains.exposed.dao.id.IntIdTable

object GuruTable : IntIdTable("guru") {

    val nama_lengkap = varchar("nama_lengkap", 255)
    val nip = varchar("nip", 50)
    val no_telepon = varchar("no_telepon", 20)
    val anak_wali = varchar("anak_wali", 255)
    val mata_pelajaran = varchar("mata_pelajaran", 100)
    val alamat = text("alamat")

}
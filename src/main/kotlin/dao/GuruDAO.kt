package org.delcom.dao

import org.delcom.tables.GuruTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID

class GuruDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<GuruDAO>(GuruTable)

    var namaLengkap by GuruTable.nama_lengkap
    var nip by GuruTable.nip
    var noTelepon by GuruTable.no_telepon
    var anakWali by GuruTable.anak_wali
    var mataPelajaran by GuruTable.mata_pelajaran
    var alamat by GuruTable.alamat
    var jabatan by GuruTable.jabatan
}
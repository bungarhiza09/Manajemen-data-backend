package org.delcom.dao

import org.delcom.tables.SiswaTable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID

class SiswaDAO(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<SiswaDAO>(SiswaTable)

    var namaLengkap by SiswaTable.nama_lengkap
    var jurusan by SiswaTable.jurusan
    var nisn by SiswaTable.nisn
    var nis by SiswaTable.nis
    var kelas by SiswaTable.kelas
    var tanggalLahir by SiswaTable.tanggal_lahir
    var alamat by SiswaTable.alamat
    var noWaOrtu by SiswaTable.no_wa_ortu
    var status by SiswaTable.status

    var raporFile by SiswaTable.rapor_file
    var sklFile by SiswaTable.skl_file
    var ijazahFile by SiswaTable.ijazah_file
}
package org.delcom.service

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.Siswa
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.SiswaRepository
import java.io.File
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import io.ktor.http.*
import io.ktor.server.response.*

class SiswaService(
    private val siswaRepository: SiswaRepository
) {

    // ===== BASIC LOGIC =====

    suspend fun createSiswa(request: SiswaRequest): Siswa {
        return siswaRepository.create(request.toEntity())
    }

    suspend fun getAllSiswa(limit: Int, offset: Long): List<Siswa> {
        return siswaRepository.getAll(limit, offset)
    }

    suspend fun getSiswaById(id: String): Siswa? {
        return siswaRepository.getById(id)
    }

    suspend fun updateSiswa(id: String, request: SiswaRequest): Boolean {
        return siswaRepository.update(
            id,
            request.namaLengkap,
            request.jurusan,
            request.nisn,
            request.nis,
            request.kelas,
            request.tanggalLahir,
            request.alamat,
            request.noWaOrtu,
            request.status,
            request.raporFile,
            request.sklFile,
            request.ijazahFile
        )
    }

    suspend fun deleteSiswa(id: String): Boolean {
        return siswaRepository.delete(id)
    }

    // ===== HANDLER =====

    suspend fun getAll(call: ApplicationCall) {

        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val siswaList = siswaRepository.getAll(limit, offset)

        val hasMore = siswaList.size == limit

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data siswa",
                data = mapOf(
                    "siswa" to siswaList,
                    "limit" to limit,
                    "offset" to offset,
                    "hasMore" to hasMore
                )
            )
        )
    }

    suspend fun getById(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val siswa = siswaRepository.getById(id)
            ?: throw AppException(404, "Siswa tidak ditemukan")

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data",
                data = mapOf("siswa" to siswa)
            )
        )
    }

    suspend fun post(call: ApplicationCall) {

        val request = call.receive<SiswaRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("namaLengkap")
        validator.required("nisn")
        validator.required("kelas")
        validator.validate()

        val siswa = createSiswa(request)

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menambah siswa",
                data = mapOf("siswa" to siswa)
            )
        )
    }

    suspend fun put(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val request = call.receive<SiswaRequest>()

        val isUpdated = updateSiswa(id, request)

        if (!isUpdated) {
            throw AppException(400, "Gagal update siswa")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengubah data",
                data = mapOf("message" to "Siswa berhasil diupdate")
            )
        )
    }

    suspend fun delete(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val isDeleted = deleteSiswa(id)

        if (!isDeleted) {
            throw AppException(400, "Gagal delete siswa")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menghapus data",
                data = mapOf("message" to "Siswa berhasil dihapus")
            )
        )
    }

    // 🔍 SEARCH + SORT
    suspend fun search(call: ApplicationCall) {

        val keyword = call.request.queryParameters["keyword"]
        val sortBy = call.request.queryParameters["sortBy"]
        val order = call.request.queryParameters["order"] ?: "desc"

        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val data = siswaRepository.search(
            keyword,
            sortBy,
            order,
            limit,
            offset
        )

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mencari data",
                data = mapOf("siswa" to data)
            )
        )
    }

    // 📥 DOWNLOAD FILE
    suspend fun download(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val type = call.parameters["type"]
            ?: throw AppException(400, "Tipe file tidak valid")

        val siswa = siswaRepository.getById(id)
            ?: throw AppException(404, "Siswa tidak ditemukan")

        val filePath = when (type) {
            "rapor" -> siswa.raporFile
            "skl" -> siswa.sklFile
            "ijazah" -> siswa.ijazahFile
            else -> null
        } ?: throw AppException(404, "File tidak tersedia")

        val file = File(filePath)

        if (!file.exists()) {
            throw AppException(404, "File tidak ditemukan")
        }

        call.response.header(
            "Content-Disposition",
            "attachment; filename=\"${file.name}\""
        )

        call.respondFile(file)
    }

    suspend fun getStats(call: ApplicationCall) {

        val stats = siswaRepository.getStats()

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil ambil statistik",
                data = stats
            )
        )
    }

    suspend fun exportExcel(call: ApplicationCall) {

        val data = siswaRepository.getAllForExport()

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data Siswa")

        // HEADER
        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("NISN")
        header.createCell(1).setCellValue("Nama")
        header.createCell(2).setCellValue("Kelas")
        header.createCell(3).setCellValue("Jurusan")
        header.createCell(4).setCellValue("Status")

        // DATA
        data.forEachIndexed { index, siswa ->
            val row = sheet.createRow(index + 1)

            row.createCell(0).setCellValue(siswa.nisn)
            row.createCell(1).setCellValue(siswa.namaLengkap)
            row.createCell(2).setCellValue(siswa.kelas)
            row.createCell(3).setCellValue(siswa.jurusan)
            row.createCell(4).setCellValue(siswa.status)
        }

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()

        call.respondBytes(
            bytes = outputStream.toByteArray(),
            contentType = ContentType.Application.OctetStream,
            status = HttpStatusCode.OK
        )
    }
}
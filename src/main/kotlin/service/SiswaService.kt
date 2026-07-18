package org.delcom.service

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.Siswa
import org.delcom.helpers.RoleClaims
import org.delcom.helpers.Roles
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.SiswaRepository
import java.io.File
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider

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

        val limit  = call.request.queryParameters["limit"]?.toIntOrNull()  ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val siswaList    = siswaRepository.getAll(limit, offset)
        val siswaWithMeta = siswaList.map { it.toResponse() }
        val hasMore      = siswaList.size == limit

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengambil data siswa",
                data    = SiswaListResponse(
                    siswa  = siswaWithMeta,
                    limit  = limit,
                    offset = offset,
                    hasMore = hasMore
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
                status  = "success",
                message = "Berhasil mengambil data",
                data    = mapOf("siswa" to siswa)
            )
        )
    }

    suspend fun post(call: ApplicationCall) {

        val request   = call.receive<SiswaRequest>()
        val validator = ValidatorHelper(request.toMap())
        validator.required("namaLengkap")
        validator.required("nisn")
        validator.required("kelas")
        validator.validate()

        val siswa = createSiswa(request)

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil menambah siswa",
                data    = mapOf("siswa" to siswa)
            )
        )
    }

    suspend fun put(call: ApplicationCall) {

        val id      = call.parameters["id"] ?: throw AppException(400, "ID tidak valid")
        val request = call.receive<SiswaRequest>()

        if (!updateSiswa(id, request)) throw AppException(400, "Gagal update siswa")

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mengubah data",
                data    = mapOf("message" to "Siswa berhasil diupdate")
            )
        )
    }

    suspend fun delete(call: ApplicationCall) {

        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak valid")

        if (!deleteSiswa(id)) throw AppException(400, "Gagal delete siswa")

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil menghapus data",
                data    = mapOf("message" to "Siswa berhasil dihapus")
            )
        )
    }

    suspend fun search(call: ApplicationCall) {

        val keyword = call.request.queryParameters["keyword"]
        val sortBy  = call.request.queryParameters["sortBy"]
        val order   = call.request.queryParameters["order"]  ?: "desc"
        val limit   = call.request.queryParameters["limit"]?.toIntOrNull()  ?: 10
        val offset  = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val data = siswaRepository.search(keyword, sortBy, order, limit, offset)

        call.respond(
            DataResponse(
                status  = "success",
                message = "Berhasil mencari data",
                data    = mapOf("siswa" to data.map { it.toResponse() })
            )
        )
    }

    // ═══════════════════════════════════════════════════════════════
    // DOWNLOAD SKL — dua skenario, satu endpoint
    //
    // Skenario 1 — ADMIN
    //   Token berisi  : role = "admin"
    //   Bisa download : SKL siswa SIAPAPUN (semua {id})
    //
    // Skenario 2 — SISWA
    //   Token berisi  : role = "siswa", userId = id siswa yang login
    //   Bisa download : HANYA SKL MILIKNYA SENDIRI
    //                   → {id} di URL harus sama dengan userId di token
    //   Tipe yang boleh didownload siswa: hanya "skl"
    //   (rapor dan ijazah hanya bisa didownload admin)
    // ═══════════════════════════════════════════════════════════════
    suspend fun download(call: ApplicationCall) {

        val id   = call.parameters["id"]   ?: throw AppException(400, "ID tidak valid")
        val type = call.parameters["type"] ?: throw AppException(400, "Tipe file tidak valid")

        val siswa = siswaRepository.getById(id)
            ?: throw AppException(404, "Siswa tidak ditemukan")

        val filePath = when (type) {
            "rapor"  -> siswa.raporFile
            "skl"    -> siswa.sklFile
            "ijazah" -> siswa.ijazahFile
            else     -> throw AppException(400, "Tipe tidak valid. Gunakan: rapor, skl, atau ijazah")
        } ?: throw AppException(404, "File $type belum tersedia untuk siswa ini")

        val file = File(filePath)
        if (!file.exists()) throw AppException(404, "File tidak ditemukan di server")

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
                status  = "success",
                message = "Berhasil ambil statistik",
                data    = stats
            )
        )
    }

    suspend fun exportExcel(call: ApplicationCall) {

        val data     = siswaRepository.getAllForExport()
        val workbook = XSSFWorkbook()
        val sheet    = workbook.createSheet("Data Siswa")

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("NISN")
        header.createCell(1).setCellValue("Nama")
        header.createCell(2).setCellValue("Kelas")
        header.createCell(3).setCellValue("Jurusan")
        header.createCell(4).setCellValue("Status")

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
            bytes       = outputStream.toByteArray(),
            contentType = ContentType.Application.OctetStream,
            status      = HttpStatusCode.OK
        )
    }

    suspend fun uploadFile(call: ApplicationCall) {

        val id   = call.parameters["id"]   ?: throw AppException(400, "ID tidak ditemukan")
        val type = call.parameters["type"] ?: throw AppException(400, "Tipe file tidak ditemukan")

        if (type !in listOf("rapor", "skl", "ijazah")) {
            throw AppException(400, "Tipe file tidak valid. Gunakan: rapor, skl, ijazah")
        }

        val multipart = call.receiveMultipart()
        var fileName: String? = null
        var fileBytes: ByteArray? = null

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val originalName = part.originalFileName ?: "file.pdf"
                val ext = originalName.substringAfterLast(".", "pdf")

                if (ext.lowercase() != "pdf") {
                    throw AppException(400, "Hanya file PDF yang diperbolehkan")
                }

                fileName  = "${type}_${id}_${System.currentTimeMillis()}.$ext"
                fileBytes = part.streamProvider().readBytes()
            }
            part.dispose()
        }

        if (fileBytes == null || fileName == null) {
            throw AppException(400, "File tidak ditemukan dalam request")
        }

        val uploadDir = File("uploads")
        if (!uploadDir.exists()) uploadDir.mkdirs()

        val savedFile = File(uploadDir, fileName!!)
        savedFile.writeBytes(fileBytes!!)

        val filePath = "uploads/$fileName"

        val success = when (type) {
            "rapor"  -> siswaRepository.updateRaporFile(id, filePath)
            "skl"    -> siswaRepository.updateSklFile(id, filePath)
            "ijazah" -> siswaRepository.updateIjazahFile(id, filePath)
            else     -> false
        }

        if (!success) throw AppException(404, "Siswa tidak ditemukan")

        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "status"  to "success",
                "message" to "File berhasil diupload",
                "path"    to filePath
            )
        )
    }

    // ── Extension function biar tidak repetitif ──────────────────
    private fun Siswa.toResponse() = SiswaResponse(
        id           = this.id,
        namaLengkap  = this.namaLengkap,
        jurusan      = this.jurusan,
        nisn         = this.nisn,
        nis          = this.nis,
        kelas        = this.kelas,
        tanggalLahir = this.tanggalLahir.toString(),
        alamat       = this.alamat,
        noWaOrtu     = this.noWaOrtu,
        status       = this.status,
        raporFile    = this.raporFile,
        sklFile      = this.sklFile,
        ijazahFile   = this.ijazahFile
    )

    suspend fun importExcel(call: ApplicationCall) {
        val multipart = call.receiveMultipart()
        var fileBytes: ByteArray? = null

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                fileBytes = part.streamProvider().readBytes()
            }
            part.dispose()
        }

        if (fileBytes == null) throw AppException(400, "File tidak ditemukan")

        val workbook = XSSFWorkbook(fileBytes!!.inputStream())
        val sheet = workbook.getSheetAt(0)

        var imported = 0
        var skipped = 0

        // Baris 0 = header, mulai dari baris 1
        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue

            fun cell(col: Int) = row.getCell(col)?.toString()?.trim() ?: ""

            val nisn = cell(0)
            val nis = cell(1)
            val namaLengkap = cell(2)
            val kelas = cell(3)
            val jurusan = cell(4)
            val tanggalLahir = cell(5)
            val alamat = cell(6)
            val noWaOrtu = cell(7)
            val status = cell(8).ifEmpty { "aktif" }

            // Skip baris kosong
            if (namaLengkap.isEmpty() || nisn.isEmpty()) { skipped++; continue }

            val request = SiswaRequest(
                namaLengkap = namaLengkap,
                nisn = nisn,
                nis = nis,
                kelas = kelas,
                jurusan = jurusan,
                tanggalLahir = tanggalLahir,
                alamat = alamat,
                noWaOrtu = noWaOrtu,
                status = status
            )

            try {
                siswaRepository.create(request.toEntity())
                imported++
            } catch (e: Exception) {
                skipped++
            }
        }

        workbook.close()

        call.respond(
            DataResponse(
                status = "success",
                message = "Import selesai",
                data = mapOf("imported" to imported, "skipped" to skipped)
            )
        )
    }


}

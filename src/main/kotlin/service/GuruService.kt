package org.delcom.service

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.Guru
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.GuruRepository
import java.io.ByteArrayOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider

class GuruService(
    private val guruRepository: GuruRepository
) {

    // ===== BASIC LOGIC =====

    suspend fun createGuru(request: GuruRequest): Guru {
        return guruRepository.create(request.toEntity())
    }

    suspend fun getAllGuru(limit: Int, offset: Long): List<Guru> {
        return guruRepository.getAll(limit, offset)
    }

    suspend fun getGuruById(id: String): Guru? {
        return guruRepository.getById(id)
    }

    suspend fun updateGuru(id: String, request: GuruRequest): Boolean {
        return guruRepository.update(
            id,
            request.namaLengkap,
            request.nip,
            request.noTelepon,
            request.anakWali,
            request.mataPelajaran,
            request.alamat,
            request.jabatan
        )
    }

    suspend fun deleteGuru(id: String): Boolean {
        return guruRepository.delete(id)
    }

    // ===== HANDLER =====

    suspend fun getAll(call: ApplicationCall) {

        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toLongOrNull() ?: 0

        val guruList = guruRepository.getAll(limit, offset)

        val guruWithMeta = guruList.map { guru ->
            GuruResponse(
                id = guru.id,
                namaLengkap = guru.namaLengkap,
                nip = guru.nip,
                noTelepon = guru.noTelepon,
                anakWali = guru.anakWali,
                mataPelajaran = guru.mataPelajaran,
                alamat = guru.alamat,
                jabatan = guru.jabatan
            )
        }

        val hasMore = guruList.size == limit

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data guru",
                data = GuruListResponse(
                    guru = guruWithMeta,
                    limit = limit,
                    offset = offset,
                    hasMore = hasMore
                )
            )
        )
    }

    suspend fun getById(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val guru = guruRepository.getById(id)
            ?: throw AppException(404, "Guru tidak ditemukan")

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil data",
                data = mapOf("guru" to guru)
            )
        )
    }

    suspend fun post(call: ApplicationCall) {

        val request = call.receive<GuruRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("namaLengkap")
        validator.required("nip")
        validator.required("mataPelajaran")
        validator.validate()

        val guru = createGuru(request)

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menambah guru",
                data = mapOf("guru" to guru)
            )
        )
    }

    suspend fun put(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val request = call.receive<GuruRequest>()

        val isUpdated = updateGuru(id, request)

        if (!isUpdated) {
            throw AppException(400, "Gagal update guru")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengubah data",
                data = mapOf("message" to "Guru berhasil diupdate")
            )
        )
    }

    suspend fun delete(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val isDeleted = deleteGuru(id)

        if (!isDeleted) {
            throw AppException(400, "Gagal delete guru")
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil menghapus data",
                data = mapOf("message" to "Guru berhasil dihapus")
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

        val data = guruRepository.search(
            keyword,
            sortBy,
            order,
            limit,
            offset
        )

        val guruWithMeta = data.map { guru ->
            GuruResponse(
                id = guru.id,
                namaLengkap = guru.namaLengkap,
                nip = guru.nip,
                noTelepon = guru.noTelepon,
                anakWali = guru.anakWali,
                mataPelajaran = guru.mataPelajaran,
                alamat = guru.alamat,
                jabatan = guru.jabatan
            )
        }

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mencari data",
                data = mapOf("guru" to guruWithMeta)
            )
        )
    }

    suspend fun getTotalGuru(call: ApplicationCall) {

        val total = guruRepository.getTotalGuru()

        call.respond(
            DataResponse(
                status = "success",
                message = "Berhasil mengambil total guru",
                data = mapOf("total" to total)
            )
        )
    }

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

        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            fun cell(col: Int) = row.getCell(col)?.toString()?.trim() ?: ""

            val nip          = cell(0)
            val namaLengkap  = cell(1)
            val mataPelajaran = cell(2)
            val jabatan      = cell(3)
            val noTelepon    = cell(4)
            val anakWali     = cell(5)
            val alamat       = cell(6)

            if (namaLengkap.isEmpty() || nip.isEmpty()) { skipped++; continue }

            val request = GuruRequest(
                namaLengkap = namaLengkap,
                nip = nip,
                mataPelajaran = mataPelajaran,
                jabatan = jabatan,
                noTelepon = noTelepon,
                anakWali = anakWali,
                alamat = alamat
            )

            try {
                guruRepository.create(request.toEntity())
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

    suspend fun exportExcel(call: ApplicationCall) {
        val data     = guruRepository.getAll(limit = 10000, offset = 0)
        val workbook = XSSFWorkbook()
        val sheet    = workbook.createSheet("Data Guru")

        val header = sheet.createRow(0)
        header.createCell(0).setCellValue("NIP")
        header.createCell(1).setCellValue("Nama Lengkap")
        header.createCell(2).setCellValue("Mata Pelajaran")
        header.createCell(3).setCellValue("Jabatan")
        header.createCell(4).setCellValue("No Telepon")
        header.createCell(5).setCellValue("Anak Wali")
        header.createCell(6).setCellValue("Alamat")

        data.forEachIndexed { index, guru ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(guru.nip)
            row.createCell(1).setCellValue(guru.namaLengkap)
            row.createCell(2).setCellValue(guru.mataPelajaran)
            row.createCell(3).setCellValue(guru.jabatan)
            row.createCell(4).setCellValue(guru.noTelepon)
            row.createCell(5).setCellValue(guru.anakWali)
            row.createCell(6).setCellValue(guru.alamat)
        }

        val out = ByteArrayOutputStream()
        workbook.write(out)
        workbook.close()

        call.respondBytes(
            bytes       = out.toByteArray(),
            contentType = ContentType.Application.OctetStream,
            status      = HttpStatusCode.OK
        )
    }
}
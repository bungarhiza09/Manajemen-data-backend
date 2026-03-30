package org.delcom.service

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.Guru
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.GuruRepository

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
}
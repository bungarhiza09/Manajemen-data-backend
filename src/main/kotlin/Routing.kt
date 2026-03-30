package org.delcom

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.delcom.service.GuruService
import org.delcom.service.SiswaService
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val guruService: GuruService by inject()
    val siswaService: SiswaService by inject()

    install(StatusPages) {

        exception<AppException> { call, cause ->
            val dataMap = parseMessageToMap(cause.message)

            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data tidak valid",
                    data = if (dataMap.isEmpty()) null else dataMap.toString()
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    status = "error",
                    message = cause.message ?: "Unknown error",
                    data = ""
                )
            )
        }
    }

    routing {

        get("/") {
            call.respondText("API Sistem Informasi SMKN 3 Balige Berjalan")
        }

        // =========================
        // GURU
        // =========================
        route("/guru") {

            get {
                guruService.getAll(call)
            }

            get("/search") {
                guruService.search(call)
            }

            get("/{id}") {
                guruService.getById(call)
            }

            post {
                guruService.post(call)
            }

            put("/{id}") {
                guruService.put(call)
            }

            delete("/{id}") {
                guruService.delete(call)
            }

            get("/total") {
                val total = guruService.getTotalGuru()
                call.respond(mapOf("total" to total))
            }
        }

        // =========================
        // SISWA
        // =========================
        route("/siswa") {

            get {
                siswaService.getAll(call)
            }

            get("/search") {
                siswaService.search(call)
            }

            get("/{id}") {
                siswaService.getById(call)
            }

            post {
                siswaService.post(call)
            }

            put("/{id}") {
                siswaService.put(call)
            }

            delete("/{id}") {
                siswaService.delete(call)
            }

            // 🔥 DOWNLOAD FILE
            get("/{id}/download/{type}") {
                siswaService.download(call)
            }

            get("/stats") {
                siswaService.getStats(call)
            }

            get("/export") {
                siswaService.exportExcel(call)
            }

            // 🔥 UPLOAD FILE (rapor / skl / ijazah)
            post("/{id}/upload/{type}") {
                siswaService.uploadFile(call)
            }
        }
    }
}
package org.delcom

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.RoleClaims
import org.delcom.helpers.Roles
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

        // HEALTH CHECK
        get("/") {
            call.respondText("API Sistem Informasi SMKN 3 Balige Berjalan")
        }

        // ─────────────────────────────────────────────
        // 🔥 SEMUA ROUTE TANPA AUTH SEMENTARA
        // ─────────────────────────────────────────────

        route("/guru") {
            get { guruService.getAll(call) }
            get("/search") { guruService.search(call) }
            get("/total") { guruService.getTotalGuru(call) }
            get("/{id}") { guruService.getById(call) }
            post { guruService.post(call) }
            put("/{id}") { guruService.put(call) }
            delete("/{id}") { guruService.delete(call) }
            get("/export") { guruService.exportExcel(call) }    // ✅ tambah ini
            post("/import") { guruService.importExcel(call) }
        }

        route("/siswa") {
            get { siswaService.getAll(call) }
            get("/search") { siswaService.search(call) }
            get("/stats") { siswaService.getStats(call) }
            get("/export") { siswaService.exportExcel(call) }
            post("/import") { siswaService.importExcel(call) }
            get("/{id}") { siswaService.getById(call) }
            post { siswaService.post(call) }
            put("/{id}") { siswaService.put(call) }
            delete("/{id}") { siswaService.delete(call) }

            post("/{id}/upload/{type}") {
                siswaService.uploadFile(call)
            }

            get("/{id}/download/{type}") {
                siswaService.download(call)
            }
        }
    }
}

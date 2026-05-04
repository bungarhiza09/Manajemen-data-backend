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

        // Health check — bebas akses tanpa token
        get("/") {
            call.respondText("API Sistem Informasi SMKN 3 Balige Berjalan")
        }

        // ════════════════════════════════════════════════════════
        // SEMUA ROUTE DI BAWAH INI BUTUH TOKEN (Authorization header)
        // ════════════════════════════════════════════════════════
        authenticate(JWTConstants.NAME) {

            // ────────────────────────────────────────────────────
            // GURU — hanya admin yang boleh akses
            // ────────────────────────────────────────────────────
            route("/guru") {

                get            { requireAdmin(call) { guruService.getAll(call) } }
                get("/search") { requireAdmin(call) { guruService.search(call) } }
                get("/total")  { requireAdmin(call) { guruService.getTotalGuru(call) } }
                get("/{id}")   { requireAdmin(call) { guruService.getById(call) } }
                post           { requireAdmin(call) { guruService.post(call) } }
                put("/{id}")   { requireAdmin(call) { guruService.put(call) } }
                delete("/{id}"){ requireAdmin(call) { guruService.delete(call) } }
            }

            // ────────────────────────────────────────────────────
            // SISWA — CRUD hanya admin
            // ────────────────────────────────────────────────────
            route("/siswa") {

                get            { requireAdmin(call) { siswaService.getAll(call) } }
                get("/search") { requireAdmin(call) { siswaService.search(call) } }
                get("/stats")  { requireAdmin(call) { siswaService.getStats(call) } }
                get("/export") { requireAdmin(call) { siswaService.exportExcel(call) } }
                get("/{id}")   { requireAdmin(call) { siswaService.getById(call) } }
                post           { requireAdmin(call) { siswaService.post(call) } }
                put("/{id}")   { requireAdmin(call) { siswaService.put(call) } }
                delete("/{id}"){ requireAdmin(call) { siswaService.delete(call) } }

                // Upload file — hanya admin
                post("/{id}/upload/{type}") {
                    requireAdmin(call) { siswaService.uploadFile(call) }
                }

                // ─────────────────────────────────────────────────
                // DOWNLOAD SKL — ada DUA skenario:
                //
                // 1. Admin   → bisa download SKL siswa SIAPAPUN
                //              GET /siswa/{id}/download/skl
                //              Header: Authorization: Bearer <token_admin>
                //
                // 2. Siswa   → hanya bisa download SKL MILIKNYA SENDIRI
                //              GET /siswa/{id}/download/skl
                //              Header: Authorization: Bearer <token_siswa>
                //              (validasi id di token harus cocok dengan {id} di URL)
                //
                // Keduanya pakai endpoint yang SAMA — bedanya hanya dicek di dalam
                // siswaService.download() berdasarkan role di JWT.
                // ─────────────────────────────────────────────────
                get("/{id}/download/{type}") {
                    siswaService.download(call)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Helper: cek apakah yang request adalah admin.
// Kalau bukan, langsung tolak dengan 403 Forbidden.
// ─────────────────────────────────────────────────────────────────
private suspend fun requireAdmin(call: ApplicationCall, block: suspend () -> Unit) {
    val principal = call.principal<JWTPrincipal>()
    val role = principal?.payload?.getClaim(RoleClaims.ROLE_CLAIM)?.asString()

    if (role != Roles.ADMIN) {
        call.respond(
            HttpStatusCode.Forbidden,
            mapOf(
                "status" to "error",
                "message" to "Akses ditolak. Hanya admin yang bisa melakukan ini."
            )
        )
        return
    }
    block()
}

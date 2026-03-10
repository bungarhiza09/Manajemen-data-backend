package org.delcom

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.service.GuruService
import org.delcom.service.SiswaService
import org.koin.ktor.ext.inject
import io.ktor.server.request.*

fun Application.configureRouting() {

    val guruService: GuruService by inject()
    val siswaService: SiswaService by inject()

    routing {

        get("/") {
            call.respondText("API Sistem Informasi SMKN 3 Balige Berjalan")
        }

        //-----------------------------
        // GURU
        //-----------------------------
        route("/guru") {

            get {
                val data = guruService.getAll()
                call.respond(data)
            }

            get("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID tidak valid")
                    return@get
                }

                val guru = guruService.getById(id)

                if (guru == null) {
                    call.respond(HttpStatusCode.NotFound, "Guru tidak ditemukan")
                } else {
                    call.respond(guru)
                }
            }

            post {

                val guru = call.receive<Guru>()

                val result = guruService.create(guru)

                call.respond(HttpStatusCode.Created, result)
            }

            put("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                val guru = call.receive<Guru>()

                val result = guruService.update(id, guru)

                call.respond(result)
            }

            delete("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                guruService.delete(id)

                call.respond(HttpStatusCode.OK, "Guru berhasil dihapus")
            }
        }

        //-----------------------------
        // SISWA
        //-----------------------------
        route("/siswa") {

            get {

                val data = siswaService.getAll()

                call.respond(data)
            }

            get("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID tidak valid")
                    return@get
                }

                val siswa = siswaService.getById(id)

                if (siswa == null) {
                    call.respond(HttpStatusCode.NotFound, "Data tidak ditemukan")
                } else {
                    call.respond(siswa)
                }
            }

            post {

                val siswa = call.receive<Siswa>()

                val result = siswaService.create(siswa)

                call.respond(HttpStatusCode.Created, result)
            }

            put("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }

                val siswa = call.receive<Siswa>()

                val result = siswaService.update(id, siswa)

                call.respond(result)
            }

            delete("/{id}") {

                val id = call.parameters["id"]?.toIntOrNull()

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }

                siswaService.delete(id)

                call.respond(HttpStatusCode.OK, "Siswa berhasil dihapus")
            }

            //-------------------------
            // DOKUMEN SISWA
            //-------------------------

            get("/{id}/rapor") {

                val id = call.parameters["id"]!!.toInt()

                val file = siswaService.getRaporFile(id)

                call.respondFile(file)
            }

            get("/{id}/skl") {

                val id = call.parameters["id"]!!.toInt()

                val file = siswaService.getSklFile(id)

                call.respondFile(file)
            }

            get("/{id}/ijazah") {

                val id = call.parameters["id"]!!.toInt()

                val file = siswaService.getIjazahFile(id)

                call.respondFile(file)
            }
        }
    }
}
package org.delcom.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.model.Siswa
import org.delcom.service.SiswaService

fun Route.siswaRoutes() {

    val service = SiswaService()

    route("/siswa") {

        get {
            call.respond(service.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            call.respond(service.getById(id)!!)
        }

        post {
            val siswa = call.receive<Siswa>()
            service.create(siswa)
            call.respond("Siswa berhasil ditambahkan")
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val siswa = call.receive<Siswa>()
            service.update(id, siswa)
            call.respond("Siswa berhasil diupdate")
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            service.delete(id)
            call.respond("Siswa berhasil dihapus")
        }

        get("/{id}/skl") {

            val id = call.parameters["id"]!!.toInt()
            val path = service.getSklFile(id)

            if (path == null) {
                call.respondText("SKL tidak ditemukan")
                return@get
            }

            val file = java.io.File(path)

            if (!file.exists()) {
                call.respondText("File tidak ada")
                return@get
            }

            call.respondFile(file)
        }

        get("/{id}/rapor") {

            val id = call.parameters["id"]!!.toInt()
            val path = service.getRaporFile(id)

            if (path == null) {
                call.respondText("Rapor tidak ditemukan")
                return@get
            }

            val file = java.io.File(path)

            if (!file.exists()) {
                call.respondText("File tidak ada")
                return@get
            }

            call.respondFile(file)
        }

        get("/{id}/ijazah") {

            val id = call.parameters["id"]!!.toInt()
            val path = service.getIjazahFile(id)

            if (path == null) {
                call.respondText("Ijazah tidak ditemukan")
                return@get
            }

            val file = java.io.File(path)

            if (!file.exists()) {
                call.respondText("File tidak ada")
                return@get
            }

            call.respondFile(file)
        }
    }
}
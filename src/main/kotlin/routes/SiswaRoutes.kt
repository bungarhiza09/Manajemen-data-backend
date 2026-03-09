package routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.Siswa
import service.SiswaService

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
    }
}
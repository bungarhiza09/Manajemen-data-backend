package org.delcom.routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.model.Guru
import org.delcom.service.GuruService

fun Route.guruRoutes() {

    val service = GuruService()

    route("/guru") {

        get {
            call.respond(service.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            call.respond(service.getById(id)!!)
        }

        post {
            val guru = call.receive<Guru>()
            service.create(guru)
            call.respond("Guru berhasil ditambahkan")
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val guru = call.receive<Guru>()
            service.update(id, guru)
            call.respond("Guru berhasil diupdate")
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            service.delete(id)
            call.respond("Guru berhasil dihapus")
        }
    }
}
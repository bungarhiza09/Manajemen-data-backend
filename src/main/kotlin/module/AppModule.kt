package org.delcom.module

import org.delcom.database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import org.delcom.routes.guruRoutes
import org.delcom.routes.siswaRoutes

fun Application.appModule() {

    // Initialize database
    DatabaseFactory.init()

    // Install JSON serialization
    install(ContentNegotiation) {
        json()
    }

    // Routing API
    routing {

        route("/api") {

            guruRoutes()
            siswaRoutes()

        }

    }
}
import database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import routes.guruRoutes
import routes.siswaRoutes

fun Application.module() {

    DatabaseFactory.init()

    install(ContentNegotiation) {
        json()
    }

    routing {

        guruRoutes()
        siswaRoutes()

    }
}
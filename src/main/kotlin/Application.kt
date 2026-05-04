package org.delcom

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.datetime.Instant
import kotlinx.datetime.serializers.InstantIso8601Serializer
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.RoleClaims
import org.delcom.helpers.Roles
import org.delcom.helpers.configureDatabases
import org.delcom.module.appModule
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    val dotenv = dotenv {
        directory = "."
        // Di Docker tidak ada .env — env langsung dari docker-compose
        ignoreIfMissing = true
    }
    dotenv.entries().forEach { System.setProperty(it.key, it.value) }
    EngineMain.main(args)
}

fun Application.module() {

    val jwtSecret = environment.config.property("ktor.jwt.secret").getString()

    install(Authentication) {

        // ── auth untuk ADMIN saja ──────────────────────────────────────
        jwt(JWTConstants.NAME) {
            realm = JWTConstants.REALM

            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(JWTConstants.ISSUER)
                    .withAudience(JWTConstants.AUDIENCE)
                    .build()
            )

            validate { credential ->
                val userId = credential.payload.getClaim(RoleClaims.USER_ID).asString()
                    ?: credential.payload.subject
                // Token valid asal ada userId — role dicek di masing-masing handler
                if (!userId.isNullOrBlank()) JWTPrincipal(credential.payload) else null
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("status" to "error", "message" to "Token tidak valid atau sudah kadaluarsa")
                )
            }
        }
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowCredentials = true
    }

    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(Instant::class, InstantIso8601Serializer)
            }
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = true
        })
    }

    install(Koin) { modules(appModule) }

    configureDatabases()
    configureRouting()
}

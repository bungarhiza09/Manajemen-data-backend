package org.delcom

import org.delcom.module.appModule
import io.ktor.server.application.*

fun Application.module() {
    appModule()
}
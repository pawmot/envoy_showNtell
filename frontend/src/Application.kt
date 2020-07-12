@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.pawmot.snt.envoy.frontendSrv

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/health") {
            call.respondText("OK", contentType = ContentType.Text.Plain)
        }

        static("/") {
            defaultResource("index.html", "static")
            resources("static")
        }
    }
}

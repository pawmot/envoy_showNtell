@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.pawmot.snt.envoy.svc.reviews

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.util.DataConversionException
import kotlinx.coroutines.time.delay
import java.time.Duration
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

val reviews = mapOf<UUID, List<BookReview>>(
        Pair(UUID.fromString("4857cbaa-4894-437e-9df6-f692d91eda72"), listOf(
                BookReview(UUID.fromString("4857cbaa-4894-437e-9df6-f692d91eda72"), UUID.randomUUID(), "Great book, would recommend!", 5),
                BookReview(UUID.fromString("4857cbaa-4894-437e-9df6-f692d91eda72"), UUID.randomUUID(), "Very nice, but has some cheap jokes", 4),
                BookReview(UUID.fromString("4857cbaa-4894-437e-9df6-f692d91eda72"), UUID.randomUUID(), "I could not understand a word", 1)
        )),
        Pair(UUID.fromString("f65f92ec-3a34-4808-8981-e882cc5033d8"), listOf(
                BookReview(UUID.fromString("f65f92ec-3a34-4808-8981-e882cc5033d8"), UUID.randomUUID(), "Nice!", 4),
                BookReview(UUID.fromString("f65f92ec-3a34-4808-8981-e882cc5033d8"), UUID.randomUUID(), "It helped me understand the topic.", 4),
                BookReview(UUID.fromString("f65f92ec-3a34-4808-8981-e882cc5033d8"), UUID.randomUUID(), ":(", 2))),
        Pair(UUID.fromString("ab2c6969-7a6c-4b9d-9003-7a71199a0290"), listOf(
                BookReview(UUID.fromString("ab2c6969-7a6c-4b9d-9003-7a71199a0290"), UUID.randomUUID(), "Excellent take on the topic!", 5),
                BookReview(UUID.fromString("ab2c6969-7a6c-4b9d-9003-7a71199a0290"), UUID.randomUUID(), "Author explained everything very clearly", 5),
                BookReview(UUID.fromString("ab2c6969-7a6c-4b9d-9003-7a71199a0290"), UUID.randomUUID(), "Ridden with typos, could not stand it :/", 3)))
)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(DataConversion) {
        convert<UUID> {
            decode { values, _ ->
                values.singleOrNull()?.let { UUID.fromString(it) }
            }

            encode { value ->
                when (value) {
                    null -> listOf()
                    is UUID -> listOf(value.toString())
                    else -> throw DataConversionException("Cannot convert $value as UUID")
                }
            }
        }
    }

    routing {
        get("/health") {
            call.respondText("OK", contentType = ContentType.Text.Plain)
        }

        get<Reviews> { reviewsReq ->
            val reviews = reviews[reviewsReq.id]
            if (reviews != null) {
                call.respond(HttpStatusCode.OK, reviews)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

@Location("/books/{id}/reviews")
data class Reviews(val id: UUID)

data class BookReview(val bookId: UUID, val id: UUID, val text: String, val rating: Int)

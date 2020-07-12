@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.pawmot.snt.envoy.svc.books

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

val books = listOf(
        Book(UUID.fromString("4857cbaa-4894-437e-9df6-f692d91eda72"), "C# in Depth", "Jon Skeet", "C# in Depth is a 2008 book by Jon Skeet which aims to help C# developers master the language. The second edition was published in 2010. The third edition was published in 2013. A fourth edition, due for release in 2018, was published by Skeet on March 23rd, 2019."),
        Book(UUID.fromString("f65f92ec-3a34-4808-8981-e882cc5033d8"), "Haskell in Depth", "Vitaly Bragilevsky", "Turn the corner from \"Haskell student\" to \"Haskell developer.\" Haskell in Depth explores the important language features and programming skills you'll need to build production-quality software using Haskell. And along the way, you'll pick up some interesting insights into why Haskell looks and works the way it does. Get ready to go deep!"),
        Book(UUID.fromString("ab2c6969-7a6c-4b9d-9003-7a71199a0290"), "Scala in Depth", "Joshua D. Suereth", "Scala in Depth is a unique new book designed to help you integrate Scala effectively into your development process. By presenting the emerging best practices and designs from the Scala community, it guides you through dozens of powerful techniques example by example.")
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

        get<Books> {
            call.respond(HttpStatusCode.OK, books.map { BookListItem(it.id, it.title) })
        }

        get<Books.Individual> { detailsReq ->
            delay(Duration.ofMillis(400))
            val book = books.firstOrNull { it.id == detailsReq.id }
            if (book != null) {
                call.respond(HttpStatusCode.OK, book)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

@Location("/books")
class Books {
    @Location("/{id}")
    data class Individual(val id: UUID)
}

data class BookListItem(val id: UUID, val title: String)
data class Book(val id: UUID, val title: String, val author: String, val description: String)

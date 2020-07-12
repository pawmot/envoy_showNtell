@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.pawmot.snt.envoy.bff

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import io.ktor.client.features.logging.*
import io.ktor.client.statement.HttpResponse
import io.ktor.util.DataConversionException
import kotlinx.coroutines.channels.Channel
import java.util.*
import kotlin.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

val tracingHeadersToPropagate = setOf(
        "X-Request-Id",
        "X-B3-TraceId",
        "X-B3-SpanId",
        "X-B3-ParentSpanId",
        "X-B3-Sampled",
        "X-B3-Flags"
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

    val client = HttpClient(CIO) {
        install(HttpTimeout) {
        }
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    routing {
        get("/health") {
            call.respondText("OK", contentType = ContentType.Text.Plain)
        }

        get<Books> {
            val books = getWithTailChop({
                client.get<List<BookListItem>>("http://localhost:8800/books") {
                    tracingHeadersToPropagate.forEach {
                        if (call.request.headers.contains(it)) {
                            val headerValue = call.request.headers[it]!!
                            log.info("$it: $headerValue")
                            headers.append(it, headerValue)
                        }
                    }
                }
            }, 200, this)
            call.respond(HttpStatusCode.OK, books)
        }

        get<Books.Details> { detailsReq ->
            val detailsDeff = async {
                getWithTailChop({
                    client.get<BookDetails>("http://localhost:8800/books/${detailsReq.id}") {
                        tracingHeadersToPropagate.forEach {
                            if (call.request.headers.contains(it)) {
                                val headerValue = call.request.headers[it]!!
                                log.info("$it: $headerValue")
                                headers.append(it, headerValue)
                            }
                        }
                    }
                }, 200, this)
            }
            val reviewsResponseDeff = async {
                client.get<HttpResponse>("http://localhost:8801/books/${detailsReq.id}/reviews") {
                    tracingHeadersToPropagate.forEach {
                        if (call.request.headers.contains(it)) {
                            val headerValue = call.request.headers[it]!!
                            log.info("$it: $headerValue")
                            headers.append(it, headerValue)
                        }
                    }
                }
            }

            val details = detailsDeff.await()
            val reviewsResponse = reviewsResponseDeff.await()

            if (reviewsResponse.status == HttpStatusCode.OK) {
                call.respond(HttpStatusCode.OK, BookView(details, reviews = reviewsResponse.receive()))
            } else {
                call.respond(HttpStatusCode.OK, BookView(details, nonFatalErrors = mapOf(Pair("reviews", reviewsResponse.status.description))))
            }
        }
    }
}

@Location("/api/books")
class Books {
    @Location("/{id}")
    data class Details(val id: UUID)
}

data class BookListItem(val id: UUID, val title: String)
data class BookDetails(val id: UUID, val title: String, val author: String, val description: String)
data class BookReview(val bookId: UUID, val id: String, val text: String, val rating: Int)
data class BookView(val details: BookDetails, val reviews: List<BookReview> = emptyList(), val nonFatalErrors: Map<String, String> = emptyMap())

suspend inline fun <reified T> getWithTailChop(crossinline op: suspend () -> T, tailChopDelayMs: Long, scope: CoroutineScope): T {
    val chan = Channel<TailChopResult>(1)
    val jobs = mutableListOf<Job>()

    val runOp = {
        scope.launch {
            val res = op()
            chan.send(Data(res))
        }
    }

    val runTimeout = {
        scope.launch {
            delay(tailChopDelayMs)
            chan.send(Timeout)
        }
    }

    jobs.add(runOp())
    jobs.add(runTimeout())

    for (res in chan) {
        when (res) {
            Timeout -> {
                jobs.add(runOp())
                jobs.add(runTimeout())
            }
            is Data<*> -> {
                jobs.forEach { it.cancel() }
                return res.d as T
            }
        }
    }

    throw RuntimeException("Could not get the data")
}

interface TailChopResult
data class Data<T>(val d: T) : TailChopResult
object Timeout : TailChopResult

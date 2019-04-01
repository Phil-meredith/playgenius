package app

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 9000
    routes.asServer(Jetty(port)).start()
}

val routes: HttpHandler = routes(
    "/hello" bind Method.GET to { request ->
    Response(Status.OK)
        .body("Hello, ${request.query("name")}!")},
    "/" bind Method.GET to {Response(Status.OK).body("Play Smarter, Play harder, Play Genius")}
)
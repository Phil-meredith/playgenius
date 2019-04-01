package app

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 9000
    routes.asServer(Jetty(port)).start()
}

val routes: HttpHandler = routes("/hello" bind GET to { request: org.http4k.core.Request ->
    org.http4k.core.Response(org.http4k.core.Status.Companion.OK)
        .body("Hello, ${request.query("name")}!")
})
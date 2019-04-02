package app

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.ResponseFilters
import org.http4k.routing.path
import java.time.Clock

fun main(args: Array<String>) {
    fun logger(message: String) = println("${Clock.systemUTC().instant()} $message")

    val audit = ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
        logger("Call to ${tx.request.uri} returned ${tx.response.status} and took ${tx.duration.toMillis()}")
    }

    val port = if (args.isNotEmpty()) args[0].toInt() else 9000
    audit.then(Routes.routes).asServer(Jetty(port)).start()
}


object Routes {
    val routes: HttpHandler = routes(
        "/hello" bind GET to { req -> Response(OK).body("Hello, ${req.query("name")}!") },
        "/{assetType}/{fileName}" bind GET to { req -> loadResponse("text/${req.path("assetType")}", "/public/${req.path("assetType")}/${req.path("fileName")}") },
        "/" bind GET to {loadResponse("text/html", "/public/html/index.html")}
    )

    private fun loadResponse(contentType: String, location: String) = Response(OK).header("content-type", contentType)
        .body(String(this.javaClass.getResourceAsStream(location).readBytes()))
}
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
import java.nio.ByteBuffer
import java.time.Clock

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 9000
    fun logger(message: String) = println("${Clock.systemUTC().instant()} $message")

    val audit = ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
        logger("Call to ${tx.request.uri} returned ${tx.response.status} and took ${tx.duration.toMillis()}")
    }

    audit.then(Routes.routes).asServer(Jetty(port)).start()
}


object Routes {
    val routes: HttpHandler = routes(
        "/hello" bind GET to { req -> Response(OK).body("Hello, ${req.query("name")}!") },
        "/{assetType}/{fileName}" bind GET to { req -> assetResponse(req.path("assetType"), req.path("fileName")) },
        "/" bind GET to { assetResponse("html", "index.html") }
    )

    private fun assetResponse(assetType: String?, fileName: String?) =
        Response(OK).header("content-type", if(assetType == "image") "image/jpeg" else "text/$assetType").withBody(assetType, fileName)

    private fun Response.withBody(assetType: String?, fileName: String?): Response =
        if (assetType == "image") body(Body(ByteBuffer.wrap(this.javaClass.getResourceAsStream("/public/image/$fileName").readBytes())))
        else body(String(this.javaClass.getResourceAsStream("/public/$assetType/$fileName").readBytes()))
}
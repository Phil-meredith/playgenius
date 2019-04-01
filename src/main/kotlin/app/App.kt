package app

import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 9000
    Routes.routes.asServer(Jetty(port)).start()
}


object Routes {
    val routes: HttpHandler = routes(
        "/hello" bind GET to { req -> Response(OK).body("Hello, ${req.query("name")}!") },
        "/" bind GET to { Response(OK).header("content-type","text/html")
            .body(String(this.javaClass.getResourceAsStream("/public/html/index.html").readBytes())) }
    )
}
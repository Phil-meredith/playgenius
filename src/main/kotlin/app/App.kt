package app

import clients.*
import org.http4k.contract.bind
import org.http4k.core.*
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.filter.ResponseFilters
import org.http4k.routing.routes
import java.lang.IllegalStateException
import java.time.Clock

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 9000
    fun logger(message: String) = println("${Clock.systemUTC().instant()} $message")

    val audit = ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
        logger("Call to ${tx.request.uri} returned ${tx.response.status} and took ${tx.duration.toMillis()}")
    }

    val readingsClient =
        FileReadingsClient( { name -> PlayGenius::class.java.getResourceAsStream("/public/readings/$name/readings.csv") },
            { name -> try{PlayGenius::class.java.getResourceAsStream("/public/readings/$name/team.csv") } catch (e: IllegalStateException){null}})
    val statsController = MatchStatsGenerator(readingsClient)

    val personalStatsClient = PersonalStatsClient()
    val playGeniusRoutes = PlayGenius(
        statsController,
        MatchClient(),
        personalStatsClient,
        TeamStatsClient(),
        UserDataClient(personalStatsClient)
    )
    val routes: HttpHandler = routes(
        "/" bind KayoRoutes().routes
        //,"/" bind playGeniusRoutes.routes
    )
    audit.then(
        routes)
        .asServer(Jetty(port)).start()
}

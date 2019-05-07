package app

import clients.FileReadingsClient
import org.http4k.core.*
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.filter.ResponseFilters
import org.http4k.template.HandlebarsTemplates
import java.time.Clock

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 9000
    fun logger(message: String) = println("${Clock.systemUTC().instant()} $message")

    val renderer = HandlebarsTemplates().HotReload("src/main/resources/hbs")

    val audit = ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
        logger("Call to ${tx.request.uri} returned ${tx.response.status} and took ${tx.duration.toMillis()}")
    }

    val readingsClient =
        FileReadingsClient { name -> Routes::class.java.getResourceAsStream("/public/readings/$name/readings.csv") }
    val statsController = StatsGenerator(readingsClient)

    audit.then(Routes(statsController, renderer ).routes).asServer(Jetty(port)).start()
}

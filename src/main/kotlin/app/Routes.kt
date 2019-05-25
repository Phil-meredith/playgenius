package app

import clients.MatchClient
import htmlTemplates.MatchTemplate
import htmlTemplates.MatchesTemplate
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.div
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Method.GET
import java.nio.ByteBuffer
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asPrettyJsonString
import org.http4k.lens.Path
import org.http4k.lens.StringBiDiMappings
import org.http4k.lens.map
import org.http4k.lens.string
import org.http4k.template.ViewModel

class Routes(private val statsGenerator: StatsGenerator, private val matchClient: MatchClient) {

    private fun Path.match() = map { match -> Match(match)}

    val routes = contract {
        routes.plusAssign(listOf(
            "/" bindContract GET to assetResponse("html", "index.html"),
            "/match"/ Path.match().of("match") bindContract GET to {match -> MatchTemplate(match).html.toOk()},
            "/matches" bindContract GET to MatchesTemplate(matchClient.getMatches()).html.toOk(),
            "/averagePosition" / Path.string().of("match") bindContract GET to { match ->
                statsGenerator.averagePosition(match).asJsonObject().asPrettyJsonString().toOk()
            },
            "/totalDistance" / Path.string().of("match") bindContract GET to { match ->
                statsGenerator.totalDistance(match).asJsonObject().asPrettyJsonString().toOk()
            },
            Path.string().of("assetType") / Path.string().of("fileName") bindContract GET to { assetType, fileName ->
                assetResponse(assetType, fileName)
            }
        ))
    }

    private fun String.toOk(): (Request) -> Response =
        { Response(Status.OK).body(this) }

    private fun assetResponse(assetType: String, fileName: String): (Request) -> Response =
        {
            Response(Status.OK).header("content-type", if (assetType == "image") "image/jpeg" else "text/$assetType")
                .withBody(
                    assetType,
                    fileName
                )
        }

    private fun Response.withBody(assetType: String?, fileName: String?): Response =
        if (assetType == "image") body(
            Body(
                ByteBuffer.wrap(
                    this.javaClass.getResourceAsStream(
                        "/public/image/$fileName"
                    ).readBytes()
                )
            )
        )
        else body(String(this.javaClass.getResourceAsStream("/public/$assetType/$fileName").readBytes()))
}

data class Matches(val value: List<Match>)
data class Match(val value: String)
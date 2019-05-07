package app

import htmlTemplates.MatchesTemplate
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import java.nio.ByteBuffer
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asPrettyJsonString
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.viewModel

class Routes(private val statsGenerator: StatsGenerator, renderer: TemplateRenderer) {
    val routes: HttpHandler = routes(
        "/averagePosition/{match}" bind GET to { req ->
            req.path("match")
                .toResponse { name -> statsGenerator.averagePosition(name).asJsonObject().asPrettyJsonString() }
        },
        "/totalDistance/{match}" bind GET to { req ->
            req.path("match")
                .toResponse { name -> statsGenerator.totalDistance(name).asJsonObject().asPrettyJsonString() }
        },
        "/hello" bind GET to { req ->
            Response(Status.OK).body("Hello, ${req.query("name")}!")
        },
        "/{assetType}/{fileName}" bind GET to { req ->
            assetResponse(
                req.path("assetType"),
                req.path("fileName")
            )
        },
        "/" bind GET to { assetResponse("html", "index.html") },
        "/matches" bind GET to {Response(Status.OK).body(renderer(Matches(listOf("1234"))))},
        "/matches2" bind GET to {Response(Status.OK).header("content-type","text/html").body(MatchesTemplate(Matches(listOf("1234"))).html)}
    )

    private fun assetResponse(assetType: String?, fileName: String?) =
        Response(Status.OK).header("content-type", if (assetType == "image") "image/jpeg" else "text/$assetType").withBody(
            assetType,
            fileName
        )

    private fun String?.toResponse(execute: (String) -> String) = this?.let {
        Response(Status.OK).body(
            execute(it)
        )
    } ?: Response(Status.NOT_FOUND)

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

data class Matches(val value: List<String>): ViewModel
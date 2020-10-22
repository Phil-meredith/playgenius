package app

import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.div
import org.http4k.core.Body
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.string
import java.nio.ByteBuffer

class KayoRoutes() {


    val routes = contract {
        routes.plusAssign(listOf(
            //"/social-stats" bindContract GET to assetResponse("html", "kayo.html"),
            "/ko" bindContract GET to assetResponse("html", "ko.html"),
            Path.string().of("assetType") / Path.string().of("fileName") bindContract GET to { assetType, fileName ->
                assetResponse(assetType, fileName)
            }
        ))
    }

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
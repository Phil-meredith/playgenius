package app

import clients.MatchClient
import htmlTemplates.MatchTemplate
import htmlTemplates.MatchesTemplate
import htmlTemplates.SimpleMatchTemplate
import htmlTemplates.components.teamStats
import model.*
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.contract.div
import org.http4k.core.*
import org.http4k.core.Method.GET
import org.http4k.format.Jackson.asJsonObject
import org.http4k.format.Jackson.asPrettyJsonString
import org.http4k.lens.Path
import org.http4k.lens.string
import java.nio.ByteBuffer

class PersonalStatsClient{
    fun personalStats(userId: UserId) = PersonalStats(listOf(
        Stats("Quickest Speed","10ms"),
        Stats("Furthest Run Speed","2k"),
        Stats("Most Goals Scored","5")),
        listOf(
            Stats("Average Speed","10ms"),
            Stats("Average distance ","2k"),
            Stats("Average Goals Scored","5")
        ),
        listOf(Stats("All time Goals Scored","25"),
            Stats("All time quickest speed","12ms"),
            Stats("All time total run","100k")
        ))
}

class TeamStatsClient{
    fun teamStats(teamId: TeamId) = TeamStats(listOf(
        Stats("Played","12"),
        Stats("Wins","10"),
        Stats("Loses","2"),
        Stats("Draws","0"),
        Stats("Fastest player","Sam"),
        Stats("Best goalscore","Marcus"),
        Stats("Most games played","Matt")
    ))
}

class Routes(private val statsGenerator: MatchStatsGenerator,
             private val matchClient: MatchClient,
             private val personalStatsClient: PersonalStatsClient,
             private val teamStatsClient: TeamStatsClient) {

    private fun Path.matchId() = map { match -> MatchId(match) }

    val routes = contract {
        routes.plusAssign(listOf(
            "/" bindContract GET to assetResponse("html", "index.html"),
            "/match" / Path.matchId().of("match") bindContract GET to { match -> MatchTemplate(match).html.toOk() },
            "/simpleMatch" / Path.matchId().of("match")   bindContract GET to {match ->
                SimpleMatchTemplate(
                    match,
                    matchClient.getTeamFor(match),
                    statsGenerator.maximumSpeed(match),
                    statsGenerator.totalDistance(match),
                    matchClient.matchResult()
                ).html.toOk()
            },
            "/matches" bindContract GET to MatchesTemplate(
                matchClient.getMatches(),
                personalStatsClient.personalStats(UserId("123")),
                teamStatsClient.teamStats(TeamId("123"))
                ).html.toOk(),

            "/averagePosition" / Path.string().of("match") bindContract GET to { match ->
                statsGenerator.averagePosition(match).asJsonObject().asPrettyJsonString().toOk()
            },
            "/team" / Path.matchId().of("match") bindContract GET to { match ->
                matchClient.getTeamFor(match).asJsonObject().asPrettyJsonString().toOk()
            },
            "/totalDistance" / Path.matchId().of("match") bindContract GET to { match ->
                statsGenerator.totalDistance(match).entries.associate { (k, y) ->k.value to  y.value }
                    .asJsonObject().asPrettyJsonString().toOk()
            },
            "/cumulativeDistance" / Path.matchId().of("match") bindContract GET to { match ->
                statsGenerator.cumulativeDistance(match)
                    .toCumulativeDistanceJsonString()
                    .toOk()
            },

            "/maximumSpeed" / Path.matchId().of("match") bindContract GET to { match ->
                statsGenerator.maximumSpeed(match)
                    .map{ (userId, maxSpeed) -> userId.value to maxSpeed.distance.value }
                    .toMap()
                    .asJsonObject()
                    .asPrettyJsonString()
                    .toOk()
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

    private fun Map<UserId, List<DistanceAtTime>>.toCumulativeDistanceJsonString() =
        entries.associate { (k, v) -> k.value to v.map { d -> mapOf("x" to d.time, "y" to d.distance.value) } }
            .asJsonObject().asPrettyJsonString()
}
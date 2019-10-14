package htmlTemplates

import clients.GameResult
import clients.Team
import htmlTemplates.components.splashHeader
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import model.Distance
import model.DistanceAtTime
import model.MatchId
import model.UserId
import kotlin.math.round


class SimpleMatchTemplate(
    private val matchId: MatchId,
    private val team: Team,
    private val maxSpeed: Map<UserId, DistanceAtTime>,
    private val totalDistance: Map<UserId, Distance>,
    private val score: GameResult
) {

    val html = createHTML().html {
        head {
            link("https://fonts.googleapis.com/css?family=Lobster|Montserrat", rel = "stylesheet")
            link("https://fonts.googleapis.com/css?family=Lobster", rel = "stylesheet")
            link("/css/main.css", rel = "stylesheet", type = "text/css")
            meta("viewport", "width=device-width, initial-scale=1.0")
            meta("matchId", matchId.value)
        }
        body {
            splashHeader {}
            div("match") {
                div("match-info") {
                    div("result"){
                        h1{+"Result"}
                        h3{span { +"Home ${score.us}" }
                            span("divider"){+"-"}
                            span{+"Away ${score.opposition}"}}
                    }
                    div("team") {
                        h1 { +"Team" }
                        ul {
                            team.players.map { player -> li { +player.capitalize() } }
                        }
                    }
                }
                div("stats") {
                    div("total-distance") {
                        h1 { +"Total Distance" }
                        ul {
                            totalDistance.entries.sortedByDescending { it.value.value }.map { (player, distance) ->
                                li {
                                    span("name") { +player.value.capitalize()  }
                                    span("player-distance") { +"${distance.value.round(2)}" }
                                }
                            }
                        }
                    }
                    div("maximum-speed") {
                        h1 { +"Maximum Speed" }
                        ul {
                            maxSpeed.entries.sortedByDescending { it.value.distance.value }.map { (player, distance) ->
                                li {
                                    span("name") { +player.value.capitalize()  }
                                    span("max-speed") { +"${distance.distance.value.round(2)}" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}



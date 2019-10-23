package htmlTemplates

import htmlTemplates.components.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import model.Matches
import model.PersonalStats
import model.TeamStats


class MatchesTemplate(
    private val matches: Matches,
    private val stats: PersonalStats,
    private val teamStats: TeamStats
) {

    val html = createHTML().html {
        head {
            link("https://fonts.googleapis.com/css?family=Lobster|Montserrat", rel = "stylesheet")
            link("https://fonts.googleapis.com/css?family=Lobster", rel = "stylesheet")
            link("/css/main.css", rel = "stylesheet", type = "text/css")
            meta("viewport", "width=device-width, initial-scale=1.0")
        }
        body {
            splashHeader { loggedInNavBar() }
            div ("matches"){
                listSection("Your Matches", "results") { yourMatchesCard(matches,"boot-small.png")(this) }

                    statsSection(
                        "Your Seasons Stats", "matches__stats__personal","matches__stats__personal__details", listOf(
                            allYourStatsCard("Best", stats.bestStats, "trophy-small.png"),
                            allYourStatsCard("Average", stats.average, "flatline-small.png"),
                            allYourStatsCard("Cumulative", stats.cumulative, "chart-small.png")
                        )
                    )
                    listSection("Team Stats", "matches__stats__team") { teamStatsCard(teamStats,"block block--medium")(this) }

            }
        }
    }
}



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
            div ("match"){
                listSection("Your Matches", "results", { yourMatches(matches)() })
                div("stats") {
                    statsSection(
                        "Your Stats", "result", listOf(
                            allYourStats("Best", stats.bestStats),
                            allYourStats("Average", stats.average),
                            allYourStats("Cumulative", stats.allTime)
                        )
                    )
                    listSection("Team Stats", "result", { teamStats(teamStats)() })
                }

            }
        }
    }
}



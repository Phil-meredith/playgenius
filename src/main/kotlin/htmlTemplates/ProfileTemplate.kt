package htmlTemplates

import htmlTemplates.components.loggedInNavBar
import htmlTemplates.components.splashHeader
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import model.MatchId
import model.UserData
import model.UserId

class ProfileTemplate(private val userData: UserData) {

    val html = createHTML().html {
        head {
            link("https://fonts.googleapis.com/css?family=Lobster|Montserrat", rel = "stylesheet")
            link("https://fonts.googleapis.com/css?family=Lobster", rel = "stylesheet")
            link("/css/main.css", rel = "stylesheet", type = "text/css")
            meta("viewport", "width=device-width, initial-scale=1.0")
            script(type= ScriptType.textJScript, src = "https://cdn.jsdelivr.net/npm/chart.js@2.8.0/dist/Chart.min.js"){}
            script(type= ScriptType.textJScript, src = "/js/main.js"){}
        }
        body {
            splashHeader{loggedInNavBar()}
            article("profile") {
                div("stats-card") {
                    div("stats-card__circular") {img(src="/image/${userData.imgUrl ?: "billy_hughes.jpg"}")  }
                    div("player-info"){
                        userData.nickname?.apply { h1{ + userData.nickname} }
                        h1 { + "${userData.firstName} ${userData.surname}"  }
                        h3 { + "${userData.position}"  }
                    }
                    div("stats"){
                        table {
                            thead {
                                tr {
                                    th { +"Yr" }
                                    th { +"Team" }
                                    th { +"Win" }
                                    th { +"Lose" }
                                    th { +"Draw" }
                                    th { +"Goals" }
                                }
                            }
                            tbody {
                                userData.matchStats.forEach {stat ->
                                    tr{
                                        td { + stat.season }
                                        td { + stat.teamName }
                                        td { + stat.wins.toString() }
                                        td { + stat.loses.toString() }
                                        td { + stat.draw.toString()}
                                        td { + stat.goalsScored.toString() }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



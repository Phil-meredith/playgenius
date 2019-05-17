package htmlTemplates

import app.Match
import kotlinx.html.*
import kotlinx.html.stream.createHTML


class MatchTemplate(private val match: Match) {

    val html = createHTML().html {
        head {
            link("https://fonts.googleapis.com/css?family=Lobster|Montserrat", rel = "stylesheet")
            link("https://fonts.googleapis.com/css?family=Lobster", rel = "stylesheet")
            link("/css/main.css", rel = "stylesheet", type = "text/css")
            meta("viewport", "width=device-width, initial-scale=1.0")
            meta("matchId", match.value)
            script(type= ScriptType.textJScript, src = "https://cdn.jsdelivr.net/npm/chart.js@2.8.0/dist/Chart.min.js"){}
            script(type= ScriptType.textJScript, src = "/js/main.js"){}
        }
        body {
            splashHeader{}
            article {
               div{
                   id = "charts"
                   canvas {
                       id="totalDistance"
                       width="200"
                       height="200"
                   }
               }
            }
        }
    }
}



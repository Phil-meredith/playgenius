package htmlTemplates

import app.Matches
import kotlinx.html.*
import kotlinx.html.stream.createHTML


class MatchesTemplate(private val matches: Matches) {

    val html = createHTML().html {
        head {
            link("https://fonts.googleapis.com/css?family=Lobster|Montserrat", rel = "stylesheet")
            link("https://fonts.googleapis.com/css?family=Lobster", rel = "stylesheet")
            link("/css/main.css", rel = "stylesheet", type = "text/css")
            meta("viewport", "width=device-width, initial-scale=1.0")
        }
        body {
            splashHeader{}
            article {
                ul {
                    matches.value.forEach {
                        li {
                            a(href="/simpleMatch/${it.value}"){+it.value}
                        }
                    }
                }
            }
        }
    }
}



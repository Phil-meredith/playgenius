package htmlTemplates.components

import kotlinx.html.*
import model.Matches
import model.Stats
import model.TeamStats

fun DIV.listSection(heading: String, css: String, stuff: UL.() -> Unit): Unit {
    div(css) {
        h1 { +heading }
        ul { stuff() }
    }
}

fun DIV.statsSection(heading: String, css: String, stuff: List<UL.() -> Unit>): Unit {
    div(css) {
        h1 { +heading }
        stuff.forEach { ul { it() } }
    }
}

fun yourMatches(matches: Matches): UL.() -> Unit = {
    matches.value.forEach {
        this.li {
            this.a(href = "/simpleMatch/${it.value}") { +it.value.toString() }
        }
    }
}

fun allYourStats(heading: String, stats: List<Stats>): UL.() -> Unit = {
    h3 { +heading }
    stats.forEach { stat ->
        this.li {
            span { +stat.key }
            span { +" : " }
            span { +stat.value }
        }
    }
}

fun teamStats(stats: TeamStats): UL.() -> Unit = {
    stats.value.forEach { stat ->
        this.li {
            span { +stat.key }
            span { +" : " }
            span { +stat.value }
        }
    }
}
package htmlTemplates.components

import kotlinx.html.*
import model.Matches
import model.Stats
import model.TeamStats

fun DIV.listSection(heading: String, css: String, stuff: DIV.() -> Unit): Unit {
    div(css) {
        h1 { +heading }
        stuff()
    }
}

fun DIV.statsSection(heading: String, containerCss: String, css: String, stuff: List<DIV.() -> Unit>): Unit {
    div(containerCss) {
        h1 { +heading }
        div(css) {
            stuff.forEach { it() }
        }
    }
}

fun yourMatchesCard(matches: Matches, cardImage: String): DIV.() -> Unit = {
    div("matches-block matches-block--shadow") {
        div("matches-block__image") { img(src = "/image/$cardImage") {} }
        ul {
            matches.value.forEach {
                this.li {
                    this.a(href = "/simpleMatch/${it.value}") { +it.value.toString() }
                }
            }
            li{ a(href ="/"){+"All Matches"}}
        }
    }
    }

fun allYourStatsCard(heading: String, stats: List<Stats>, cardImage: String? = null): DIV.() -> Unit = {
    div("stats-block stats-block--shadow") {
        cardImage?.apply { div("stats-block__image"){img (src = "/image/$cardImage"){}} }
       div {
           h3 { +heading }
           table {
               thead { tr { stats.map { it.key }.forEach { th { +it } } } }
               tbody { tr { stats.map { it.value }.forEach { td { +it } } } }
           }
       }
    }
}

fun teamStats(stats: TeamStats): DIV.() -> Unit = {
    table {
        thead { tr { stats.value.map { it.key }.forEach { th { +it } } } }
        tbody { tr { stats.value.map { it.value }.forEach { td { +it } } } }
    }
}
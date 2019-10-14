package htmlTemplates.components

import kotlinx.html.*
import model.Matches
import model.MicroType

fun ARTICLE.listSection(heading: String, matches: Matches, stuff: () -> Unit): Unit {
    h3 { +heading }
    ul { toLi(matches.value) }
}

private fun <T : MicroType> UL.toLi(matches: List<T>) {
    matches.forEach {
        li {
            a(href = "/simpleMatch/${it.value}") { + it.value.toString() }
        }
    }
}
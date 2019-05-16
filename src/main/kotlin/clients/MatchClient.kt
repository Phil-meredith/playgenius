package clients

import app.Match
import app.Matches

class MatchClient {
    fun getMatches(): Matches = Matches(listOf(Match("150319"), Match("250319")))
}
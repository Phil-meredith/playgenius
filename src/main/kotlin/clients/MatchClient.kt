package clients

import app.MatchId
import app.Matches

class MatchClient {
    fun getMatches(): Matches = Matches(listOf(MatchId("150319"), MatchId("250319")))
}
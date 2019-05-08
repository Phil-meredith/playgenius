package clients

import app.Matches

class MatchClient {
    fun getMatches(): Matches = Matches(listOf("150319","250319"))
}
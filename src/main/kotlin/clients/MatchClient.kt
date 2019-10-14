package clients

import model.MatchId
import model.Matches

class MatchClient {
    fun getMatches(): Matches = Matches(listOf(MatchId("150319"), MatchId("250319")))

    fun getTeamFor(matchId: MatchId): Team  = Team(listOf("Sam", "Matt", "Marcus", "Ross"))

    fun matchResult(): GameResult = GameResult(6, 3)
}

data class GameResult(val us: Int, val opposition: Int)

data class Team (val players: List<String>)
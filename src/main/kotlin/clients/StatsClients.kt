package clients

import model.*

class PersonalStatsClient{
    fun personalStats(userId: UserId) = PersonalStats(
        listOf(
            Stats("Speed", "10ms"),
            Stats("Distance", "2k"),
            Stats("Goals Scored", "5")
        ),
        listOf(
            Stats("Goals Scored", "25"),
            Stats("Total run", "100k")
        ),
        listOf(
            Stats("Speed", "10ms"),
            Stats("Distance ", "2k"),
            Stats("Goals Scored", "5")
        )
    )

}

class TeamStatsClient{
    fun teamStats(teamId: TeamId) = TeamStats(
        listOf(
            Stats("Played", "12"),
            Stats("Wins", "10"),
            Stats("Loses", "2"),
            Stats("Draws", "0"),
            Stats("Fastest player", "Sam"),
            Stats("Best goalscore", "Marcus"),
            Stats("Most games played", "Matt")
        )
    )
}
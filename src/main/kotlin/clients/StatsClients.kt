package clients

import model.*
import java.time.Year

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

    fun individualMatchStats(userId: UserId):  List<ProfileStat> = listOf(
        ProfileStat("19/20","Dons", 4,2,0,12),
        ProfileStat("18/19","Dons", 6,0,0,18),
        ProfileStat("17/18","Dons", 2,3,1,7)
    )
}

data class ProfileStat(val season: String, val teamName: String, val wins: Int, val loses: Int, val draw: Int, val goalsScored: Int )

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

class UserDataClient(private val statsClient: PersonalStatsClient){
    fun userDataFor(userId: UserId) = UserData(
        "Sam",
        "Owen",
        "Western",
        "Midfield",
        null,
        statsClient.individualMatchStats(userId)
        )
}
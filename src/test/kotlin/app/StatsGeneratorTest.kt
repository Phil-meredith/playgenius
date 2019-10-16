package app

import clients.Reading
import clients.ReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import model.Distance
import model.DistanceAtTime
import model.MatchId
import model.UserId
import org.junit.Test
import java.time.Instant


class StatsGeneratorTest {
    val startDate = Instant.now()

    val reading = Reading(startDate, UserId("0"), "blah", Triple(15.0, 1.5, 1.0), 0, 0)

    @Test
    fun `can calculate cumulative distance`() {
        val nextTime = startDate.plusSeconds(60)
        val lastTime = startDate.plusSeconds(120)

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(dateTime = nextTime, position = Triple(5.0, 1.5, -1.0)),
                    reading.copy(dateTime = lastTime, position = Triple(6.0, 1.5, 0.0))
                ).asSequence()
            }
        }
        val stats = MatchStatsGenerator(stubReadingsClient)

        assertThat(stats.cumulativeDistance(MatchId("blah")), equalTo(
            mapOf( UserId("0") to listOf(
                DistanceAtTime(nextTime,Distance(10.0)),
                DistanceAtTime(lastTime,Distance( 11.0))))))
    }

    @Test
    fun `can generate average position for one anchor`() {

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(position = Triple(5.0, 0.5, -1.0))
                ).asSequence()
            }
        }

        val stats = MatchStatsGenerator(stubReadingsClient)
        assertThat(stats.averagePosition("not-used"), equalTo(mapOf("blah" to mapOf("0" to Triple(10.0, 1.0, 0.0)))))
    }

    @Test
    fun `can generate average position for multiple anchor`() {

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(position = Triple(5.0, 0.5, -1.0)),
                    reading.copy(userTag = UserId("two")),
                    reading.copy(userTag = UserId("two"), position = Triple(6.0, 1.0, 0.0))
                ).asSequence()
            }
        }

        val stats = MatchStatsGenerator(stubReadingsClient)
        assertThat(
            stats.averagePosition("not-used"), equalTo(
                mapOf(
                    "blah" to mapOf(
                        "0" to Triple(10.0, 1.0, 0.0),
                        "two" to Triple(10.5, 1.25, 0.5)
                    )
                )
            )
        )
    }

    @Test
    fun `difference between two points`() {
        assertThat(distanceBetween(Triple(-1.0, 1.0, 0.0), Triple(3.0, 4.0, 0.0)), equalTo(5.0))
    }

    @Test
    fun `Zero difference between the same points`() {
        assertThat(distanceBetween(Triple(-1.0, 1.0, 0.0), Triple(-1.0, 1.0, 0.0)), equalTo(0.0))
    }

    @Test
    fun `can get total difference`() {
        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    Reading(startDate, UserId ("one"), "0", Triple(-1.0, 1.0, 1.0), 0, 0),
                    Reading(startDate, UserId("one"), "0", Triple(3.0, 4.0, 1.0), 0, 1),
                    Reading(startDate, UserId("two"), "0", Triple(3.0, 4.0, 1.0), 0, 0),
                    Reading(startDate, UserId("two"), "0", Triple(-1.0, 1.0, 1.0), 0, 1),
                    Reading(startDate, UserId("three"), "0", Triple(-1.0, 1.0, 1.0), 0, 0),
                    Reading(startDate, UserId("three"), "0", Triple(3.0, 4.0, 1.0), 0, 1),
                    Reading(startDate, UserId("three"), "0", Triple(3.0, 4.0, 1.0), 0, 2),
                    Reading(startDate, UserId("three"), "0", Triple(-1.0, 1.0, 1.0), 0, 3)
                ).asSequence()
            }
        }

        val stats = MatchStatsGenerator(stubReadingsClient)

        assertThat(
            stats.totalDistance(MatchId("not-used")), equalTo(
                mapOf(
                    UserId("one") to Distance(5.0),
                    UserId("two") to Distance(5.0),
                    UserId("three") to Distance(10.0)
                )
            )
        )
    }
}
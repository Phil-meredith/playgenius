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


class MaximumSpeedTest {
    val startTime = Instant.parse("2019-10-20T10:10:00Z")
    val reading = Reading(startTime, UserId("0"), "blah", Triple(15.0, 1.5, 1.0), 0, 0)

    @Test
    fun `can calculate maximum speed per second`(){

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(dateTime = startTime.plusSeconds(1), position = Triple(5.0, 1.5, -1.0)),
                    reading.copy(dateTime = startTime.plusSeconds(2), position = Triple(7.0, 1.5, 0.0))
                ).asSequence()
            }
        }
        val stats = MatchStatsGenerator(stubReadingsClient)
        assertThat(stats.maximumSpeed(MatchId("blah")), equalTo(mapOf( UserId("0") to
            DistanceAtTime(startTime.plusSeconds(2), Distance(10.0))
        )))
    }

    @Test
    fun `can calculate maximum speed per second with multiple readings per second`(){

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    reading.copy(dateTime = startTime),
                    reading.copy(dateTime = startTime.plusMillis(10), position = Triple(14.0, 1.5, -1.0)),
                    reading.copy(dateTime = startTime.plusMillis(1100), position = Triple(6.0, 1.5, 0.0)),
                    reading.copy(dateTime = startTime.plusMillis(1200), position = Triple(6.0, 2.5, 0.0))
                ).asSequence()
            }
        }
        val stats = MatchStatsGenerator(stubReadingsClient)
        assertThat(stats.maximumSpeed(MatchId("blah")), equalTo(mapOf( UserId("0") to
            DistanceAtTime(Instant.parse("2019-10-20T10:10:02Z"),Distance( 9.0)))))
    }

    @Test
    fun `missing seconds what happens?`(){
        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                val dateTime = startTime.plusMillis(5100)
                return listOf(
                    reading.copy(dateTime = startTime),
                    reading.copy(dateTime = startTime.plusMillis(10), position = Triple(14.0, 1.5, -1.0)),
                    reading.copy(dateTime = dateTime, position = Triple(5.0, 1.5, 0.0))
                ).asSequence()
            }
        }
        val stats = MatchStatsGenerator(stubReadingsClient)
        assertThat(stats.maximumSpeed(MatchId("blah")), equalTo(mapOf( UserId("0") to
                DistanceAtTime(Instant.parse("2019-10-20T10:10:06Z"),Distance( 9.0)))))
    }
}
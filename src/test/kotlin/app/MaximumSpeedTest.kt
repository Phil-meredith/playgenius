package app

import clients.Reading
import clients.ReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit


class MaximumSpeedTest {
    val startTime = Instant.parse("2019-10-20T10:10:00Z")
    val reading = Reading(startTime, UserId("0"), "blah", Triple(15.0, 1.5, 1.0), 0, 0)

    @Test
    fun `can calculate maximum speed per second`(){
        val nextTime = startTime.plusSeconds(1)
        val lastTime = startTime.plusSeconds(2)

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(dateTime = nextTime, position = Triple(5.0, 1.5, -1.0)),
                    reading.copy(dateTime = lastTime, position = Triple(7.0, 1.5, 0.0))
                ).asSequence()
            }
        }
        val stats = StatsGenerator(stubReadingsClient)
        assertThat(stats.maximumSpeed(MatchId("blah")), equalTo(mapOf( UserId("0") to
            DistanceAtTime(nextTime,Distance(10.0)))))
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
        val stats = StatsGenerator(stubReadingsClient)
        assertThat(stats.maximumSpeed(MatchId("blah")), equalTo(mapOf( UserId("0") to
            DistanceAtTime(Instant.parse("2019-10-20T10:10:02Z"),Distance( 9.0)))))
    }
}
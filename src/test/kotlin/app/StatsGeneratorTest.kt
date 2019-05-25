package app

import clients.Reading
import clients.ReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.time.Instant


class StatsGeneratorTest {
    val startDate = Instant.now()

    val reading = Reading(startDate, "0", "blah", Triple(15.0, 1.5, 1.0), 0, 0)


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

        val stats = StatsGenerator(stubReadingsClient)
        assertThat(stats.averagePosition("not-used"), equalTo(mapOf("blah" to mapOf("0" to Triple(10.0, 1.0, 0.0)))))
    }

    @Test
    fun `can generate average position for multiple anchor`() {

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(matchToLoad: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(position = Triple(5.0, 0.5, -1.0)),
                    reading.copy(userTag = "two"),
                    reading.copy(userTag = "two", position = Triple(6.0, 1.0, 0.0))
                ).asSequence()
            }
        }

        val stats = StatsGenerator(stubReadingsClient)
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
                    Reading(startDate, "one", "0", Triple(-1.0, 1.0, 1.0), 0, 0),
                    Reading(startDate, "one", "0", Triple(3.0, 4.0, 1.0), 0, 1),
                    Reading(startDate, "two", "0", Triple(3.0, 4.0, 1.0), 0, 0),
                    Reading(startDate, "two", "0", Triple(-1.0, 1.0, 1.0), 0, 1),
                    Reading(startDate, "three", "0", Triple(-1.0, 1.0, 1.0), 0, 0),
                    Reading(startDate, "three", "0", Triple(3.0, 4.0, 1.0), 0, 1),
                    Reading(startDate, "three", "0", Triple(3.0, 4.0, 1.0), 0, 2),
                    Reading(startDate, "three", "0", Triple(-1.0, 1.0, 1.0), 0, 3)
                ).asSequence()
            }
        }

        val stats = StatsGenerator(stubReadingsClient)

        assertThat(
            stats.totalDistance("not-used"), equalTo(
                mapOf(
                    "one" to 5.0,
                    "two" to 5.0,
                    "three" to 10.0
                )
            )
        )
    }
}
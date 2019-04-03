package app

import clients.Reading
import clients.ReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.time.Instant

class StatsGeneratorTest {
    val reading = Reading( Instant.now(), 0, "blah", Triple(15.0, 1.5, 1.0), 0, 0)

    @Test
    fun `can generate average position for one anchor`() {

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(fileName: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(position = Triple(5.0, 0.5, -1.0))
                ).asSequence()
            }
        }

        val stats = StatsGenerator(stubReadingsClient)
        assertThat(stats.averagePosition("not-used"), equalTo(mapOf(0 to mapOf( "blah" to Triple(10.0, 1.0, 0.0)))))
    }

    @Test
    fun `can generate average position for multiple anchor`() {

        val stubReadingsClient = object : ReadingsClient {
            override fun getReadings(fileName: String): Sequence<Reading> {
                return listOf(
                    reading,
                    reading.copy(position = Triple(5.0, 0.5, -1.0)),
                    reading.copy(anchor = "two"),
                    reading.copy(anchor= "two",position = Triple(6.0, 1.0, 0.0))
                ).asSequence()
            }
        }

        val stats = StatsGenerator(stubReadingsClient)
        assertThat(stats.averagePosition("not-used"), equalTo(
            mapOf(0 to mapOf(
                "blah" to Triple(10.0, 1.0, 0.0),
                "two" to Triple(10.5, 1.25, 0.5)
            ))))
    }
}
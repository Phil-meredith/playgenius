import clients.Reading
import clients.FileReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.time.Instant

class ReadingsParserTest {

    @Test
    fun `can parse readings`() {
        val readingsClient = FileReadingsClient( { name -> javaClass.getResourceAsStream(name)},{null})

        val readings = readingsClient.getReadings("testReadings.csv").toList()
        assertThat(readings.size, equalTo(304))

        assertThat(
            readings[0],
            equalTo(
                Reading(
                    Instant.parse("2019-03-03T23:16:31.000999228Z"),
                    "9A26",
                    "0",
                    Triple(1.32, 1.38, 0.32),
                    0,
                    153
                )
            )
        )
    }
}

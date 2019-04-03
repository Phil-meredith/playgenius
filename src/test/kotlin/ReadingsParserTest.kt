import clients.Reading
import clients.ReadingsClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import java.time.Instant

class ReadingsParserTest {

    @Test
    fun `can parse readings`() {
        val readingsClient = ReadingsClient()

        val readings = readingsClient.getReadings(javaClass.getResourceAsStream("testReadings.csv"))
        assertThat(readings.size, equalTo(304))

        assertThat(
            readings[0],
            equalTo(
                Reading(
                    Instant.parse("2019-03-03T23:16:31.000999228Z"),
                    0,
                    "9A26",
                    Triple(1.32, 1.38, 0.32),
                    0,
                    153
                )
            )
        )
    }
}

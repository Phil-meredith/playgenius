import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.opencsv.CSVReader
import org.junit.Test
import java.io.InputStreamReader
import java.time.Instant
import java.lang.Long.parseLong

class ReadingsParserTest {

    @Test
    fun `can parse readings`() {
        val readingsClient = ReadingsClient()

        val readings = readingsClient.getReadings("testReadings.csv")
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

class ReadingsClient {

    fun getReadings(path: String): List<Reading> =
        CSVReader(InputStreamReader(javaClass.getResourceAsStream(path)))
            .asSequence()
            .map {
                Reading(
                    it[0].toInstant(),
                    it[2].toInt(),
                    it[3],
                    Triple(it[4].toDouble(), it[5].toDouble(), it[6].toDouble()),
                    it[7].toInt(),
                    parseLong(it[8].removePrefix("x"), 16)
                )
            }.toList()

    private fun String.toInstant() = split(".")
        .let { Instant.ofEpochSecond(it[0].toLong(), it[1].toLong()) }
}

data class Reading(
    val date: Instant,
    val user: Int,
    val anchor: String,
    val position: Triple<Double, Double, Double>,
    val confidence: Int,
    val counter: Long
)

package clients

import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Instant

interface ReadingsClient {
    fun getReadings(fileName: String): Sequence<Reading>
}

class FileReadingsClient(private val resourceLoader : (String) -> InputStream) : ReadingsClient {
    override fun getReadings(fileName: String): Sequence<Reading> =
        CSVReader(InputStreamReader(resourceLoader(fileName)))
        .asSequence()
        .map {
            Reading(
                it[0].toInstant(),
                it[2].toInt(),
                it[3],
                Triple(it[4].toDouble(), it[5].toDouble(), it[6].toDouble()),
                it[7].toInt(),
                java.lang.Long.parseLong(it[8].removePrefix("x"), 16)
            )
        }

    private fun String.toInstant() = split(".")
        .let { Instant.ofEpochSecond(it[0].toLong(), it[1].toLong()) }
}

data class Reading(
    val date: Instant,
    val anchor: Int,
    val userTag: String,
    val position: Triple<Double, Double, Double>,
    val confidence: Int,
    val counter: Long
)
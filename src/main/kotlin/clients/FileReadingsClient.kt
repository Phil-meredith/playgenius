package clients

import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Instant

interface ReadingsClient {
    fun getReadings(matchToLoad: String): Sequence<Reading>
}

class FileReadingsClient(
    private val readingsLoader: (String) -> InputStream,
    private val teamLoader: (String) -> InputStream?
) : ReadingsClient {
    override fun getReadings(matchToLoad: String): Sequence<Reading> =
        CSVReader(InputStreamReader(readingsLoader(matchToLoad)))
            .asSequence()
            .map {
                RawReading(
                    it[0].toInstant(),
                    it[2].toInt(),
                    it[3],
                    Triple(it[4].toDouble(), it[5].toDouble(), it[6].toDouble()),
                    it[7].toInt(),
                    java.lang.Long.parseLong(it[8].removePrefix("x"), 16)
                )
            }.usePlayerNames(matchToLoad)

    private fun Sequence<RawReading>.usePlayerNames(matchToLoad: String) = map {
        getPlayerNames(matchToLoad).combineReadingAndTeam(it)
    }

    private fun getPlayerNames(matchToLoad: String): Map<Int, String> = teamLoader(matchToLoad)?.let {
     CSVReader(InputStreamReader(it)).asSequence().map { Pair(it[0].toInt()-1, it[1]) }.toMap()}.orEmpty()

    private fun Map<Int, String>.combineReadingAndTeam(rawReading: RawReading): Reading =
        Reading(
            rawReading.date,
            this[rawReading.userTag] ?: rawReading.userTag.toString(),
            rawReading.anchor,
            rawReading.position,
            rawReading.confidence,
            rawReading.counter
        )


    private fun String.toInstant() = split(".")
        .let { Instant.ofEpochSecond(it[0].toLong(), it[1].toLong()) }
}

private data class RawReading(
    val date: Instant,
    val userTag: Int,
    val anchor: String,
    val position: Triple<Double, Double, Double>,
    val confidence: Int,
    val counter: Long
)

data class Reading(
    val date: Instant,
    val userTag: String,
    val anchor: String,
    val position: Triple<Double, Double, Double>,
    val confidence: Int,
    val counter: Long
)
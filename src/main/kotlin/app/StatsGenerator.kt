package app

import clients.Reading
import clients.ReadingsClient
import java.lang.Math.pow

class StatsGenerator(private val readingsClient: ReadingsClient) {
    fun averagePosition(match: String): Map<Int, Map<String, Triple<Double, Double, Double>>> =
        readingsClient.getReadings(match)
            .groupBy { it.anchor }
            .mapValues { (_, v) ->
                v.groupBy { it.userTag }
                    .mapValues { (_, v) ->
                        v.map { it.position }
                            .reduce { acc, pos ->
                                acc.copy(
                                    acc.first + pos.first,
                                    acc.second + pos.second,
                                    acc.third + pos.third
                                )
                            }.run { copy(first / v.size, second / v.size, third / v.size) }
                    }
            }

    fun totalDistance(match: String) : Map<String, Double>  {
        return readingsClient.getReadings(match)
            .groupBy { it.userTag }
            .mapValues { (_, v) ->
                    v.sortedBy { it.counter }.map{AugementedReading(it, 0.0)}
                        .reduceRight{ reading, acc ->
                            AugementedReading(
                                reading.reading,
                                acc.distance + distanceBetweenTwoPoints(reading.reading.position, acc.reading.position).sanitise()) }
                        .distance
            }
    }

    private data class AugementedReading(val reading: Reading, val distance: Double)
}

fun distanceBetweenTwoPoints(pos1: Triple<Double, Double, Double>, pos2: Triple<Double, Double, Double>): Double =
    Math.sqrt(pow(pos2.first - pos1.first,2.0) + pow(pos2.second - pos1.second,2.0))

fun Double.sanitise(): Double = if(this > 100) 0.0 else this

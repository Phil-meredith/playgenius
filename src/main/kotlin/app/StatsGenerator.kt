package app

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

    fun totalDistance(match: String): Map<String, Double> {
        return readingsClient
            .getReadings(match)
            .groupBy { it.userTag }
            .mapValues { (_, v)->
                v.sortedBy { it.counter }
                    .zipWithNext { reading1, reading2 -> distanceBetween(reading1.position, reading2.position).sanitise()
                }.sum()
            }
    }
}

fun distanceBetween(pos1: Triple<Double, Double, Double>, pos2: Triple<Double, Double, Double>): Double =
    Math.sqrt(pow(pos2.first - pos1.first, 2.0) + pow(pos2.second - pos1.second, 2.0))

private fun Double.sanitise(): Double = if (this > 100) 0.0 else this
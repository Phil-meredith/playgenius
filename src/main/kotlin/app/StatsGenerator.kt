package app

import clients.ReadingsClient

class StatsGenerator(private val readingsClient: ReadingsClient) {
    fun averagePosition(match: String): Map<Int, Map<String, Triple<Double, Double, Double>>> =
        readingsClient.getReadings(match)
            .groupBy { it.user }
            .mapValues { (_, v) ->
                v.groupBy { it.anchor }
                    .mapValues {(_, v) ->  v.map{it.position }
                    .reduce { acc, pos ->
                        acc.copy(
                            acc.first + pos.first,
                            acc.second + pos.second,
                            acc.third + pos.third
                        )
                    }.run { copy(first / v.size, second / v.size, third / v.size) }
            }}
}
package app

import clients.Reading
import clients.ReadingsClient
import java.lang.Math.pow
import java.time.Instant
import java.time.temporal.ChronoUnit

class StatsGenerator(private val readingsClient: ReadingsClient) {

    fun averagePosition(match: String): Map<String, Map<String, Triple<Double, Double, Double>>> =
        readingsClient.getReadings(match)
            .groupBy { it.anchor }
            .mapValues { (_, v) ->
                v.groupBy { it.userTag.value }
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

    fun totalDistance(matchId: MatchId): Map<UserId, Distance> = manipulateReadingsPerUser(matchId) {
        zipWithNext { reading1, reading2 ->
            distanceBetween(reading1.position, reading2.position).sanitise()
        }.sum()
    }

    fun cumulativeDistance(matchId: MatchId): Map<UserId, List<DistanceAtTime>> {
        return manipulateReadingsPerUser(matchId) {
            zipWithNext { reading1, reading2 ->
                DistanceAtTime(reading2.dateTime, distanceBetween(reading1.position, reading2.position).sanitise())
            }
                .groupBy { d -> d.time.truncatedTo(ChronoUnit.MINUTES) }
                .map { (timeAtMinute, minuteDistances) -> minuteDistances.sumDuring(timeAtMinute) }
                .cumulativeSum()
        }
    }

    fun maximumSpeed(matchId: MatchId): Map<UserId, DistanceAtTime> {
        return manipulateReadingsPerUser(matchId) {
            zipWithNext { reading1, reading2 ->
                DistanceAtTime(reading2.dateTime.truncatedTo(ChronoUnit.SECONDS), distanceBetween(reading1.position, reading2.position).sanitise())
            }
                .groupBy { d -> d.time}
                .map { (time, distances) -> distances.sumDuring(time.plusSeconds(1)) }
        }.mapValues{ (_,v) -> v.maxBy { it.distance.value }?: v[0] }
    }

    private fun List<DistanceAtTime>.sumDuring(time: Instant) =
        reduce { acc, distance -> DistanceAtTime(time, acc.distance + distance.distance) }

    private fun <R> manipulateReadingsPerUser(
        matchId: MatchId,
        applyToUserReadings: List<Reading>.() -> R
    ): Map<UserId, R> =
        readingsClient
            .getReadings(matchId.value)
            .groupBy { it.userTag }
            .mapValues { (_, v) ->
                v.sortedBy { it.dateTime }.applyToUserReadings()
            }

    private fun List<DistanceAtTime>.cumulativeSum(): List<DistanceAtTime> {
        var distanceSoFar = 0.0
        return map { distanceSoFar += it.distance.value; DistanceAtTime(it.time, Distance(distanceSoFar)) }
    }
}

fun distanceBetween(pos1: Triple<Double, Double, Double>, pos2: Triple<Double, Double, Double>): Double =
    Math.sqrt(pow(pos2.first - pos1.first, 2.0) + pow(pos2.second - pos1.second, 2.0))

private fun Double.sanitise(): Distance = if (this > 100) Distance(0.0) else Distance(this)

data class DistanceAtTime(val time: Instant, val distance: Distance)
data class MatchId(val value: String)
data class UserId(val value: String)

data class Distance(val value: Double) {
    operator fun plus(p: Distance): Distance {
        return Distance(value + p.value)
    }
}

fun Iterable<Distance>.sum(): Distance = this.reduce { acc, distance -> acc + distance }

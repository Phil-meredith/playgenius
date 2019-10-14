package model

import java.time.Instant

abstract class MicroType {
    abstract val value: Any
}

data class Matches(override val value: List<MatchId>) : MicroType()

data class DistanceAtTime(val time: Instant, val distance: Distance)
data class MatchId(override val value: String) : MicroType()
data class UserId(val value: String)

data class Distance(val value: Double) {
    operator fun plus(p: Distance): Distance {
        return Distance(value + p.value)
    }
}
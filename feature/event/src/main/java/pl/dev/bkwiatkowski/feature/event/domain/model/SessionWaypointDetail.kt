package pl.dev.bkwiatkowski.feature.event.domain.model

import java.time.LocalDateTime

data class SessionWaypointDetail(
    val id: Int,
    val waypointId: Int,
    val visitedAt: LocalDateTime,
)
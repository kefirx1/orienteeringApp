package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class BESessionWaypointDetail(
    val id: Int,
    val waypointId: Int,
    val visitedAt: LocalDateTime,
)
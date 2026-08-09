package pl.dev.bkwiatkowski.technical.backend.domain.model

import pl.dev.bkwiatkowski.common.core.location.Position

data class BEMapWaypoint(
  val id: Int,
  val label: String,
  val position: Position,
)

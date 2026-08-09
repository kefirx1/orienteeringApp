package pl.dev.bkwiatkowski.technical.backend.domain.model

data class BEMobileMap(
  val id: Int,
  val name: String,
  val description: String,
  val imageData: String,
  val waypoints: List<BEMapWaypoint>,
)

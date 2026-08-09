package pl.dev.bkwiatkowski.feature.maps.domain.model

data class MobileMap(
  val id: Int,
  val name: String,
  val description: String,
  val imageData: String,
  val waypoints: List<MapWaypoint>,
)

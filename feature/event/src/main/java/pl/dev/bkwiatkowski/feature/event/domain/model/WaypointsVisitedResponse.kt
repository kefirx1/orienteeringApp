package pl.dev.bkwiatkowski.feature.event.domain.model

data class WaypointsVisitedResponse(
  val waypoints: List<SessionWaypointDetail>,
)
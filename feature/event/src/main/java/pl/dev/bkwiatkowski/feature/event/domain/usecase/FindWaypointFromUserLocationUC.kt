package pl.dev.bkwiatkowski.feature.event.domain.usecase

import android.location.Location
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint

interface FindWaypointFromUserLocationUC : EitherUseCase<FindWaypointFromUserLocationUC.Params, MapWaypoint?> {
  data class Params(
    val currentLocation: Location,
    val waypoints: List<MapWaypoint>,
  ) : UseCase.Params
}

class FindWaypointFromUserLocationUCImpl : FindWaypointFromUserLocationUC {
  companion object {
    private const val WAYPOINT_RADIUS_METERS = 15f
  }

  override suspend fun invoke(params: FindWaypointFromUserLocationUC.Params): Either<DomainError, MapWaypoint?> = either {
    params.waypoints.find { waypoint ->
      val waypointLocation = Location("").apply {
        latitude = waypoint.position.latitude
        longitude = waypoint.position.longitude
      }

      val distanceInMeters = params.currentLocation.distanceTo(waypointLocation)
      distanceInMeters <= WAYPOINT_RADIUS_METERS
    }
  }
}

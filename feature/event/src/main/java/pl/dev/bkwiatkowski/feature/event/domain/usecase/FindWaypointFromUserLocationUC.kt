package pl.dev.bkwiatkowski.feature.event.domain.usecase

import android.location.Location
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint

interface FindWaypointFromUserLocationUC : EitherUseCase<FindWaypointFromUserLocationUC.Params, FindWaypointFromUserLocationUC.Result> {
  data class Params(
    val currentLocation: Location,
    val waypoints: List<MapWaypoint>,
    val waypointRadiusMeters: Float,
  ) : UseCase.Params

  data class Result(
    val accuracy: Float,
    val foundWaypoint: MapWaypoint?,
  )
}

class FindWaypointFromUserLocationUCImpl : FindWaypointFromUserLocationUC {

  override suspend fun invoke(params: FindWaypointFromUserLocationUC.Params): Either<DomainError, FindWaypointFromUserLocationUC.Result> = either {
    val accuracy = params.currentLocation.accuracy

    val foundWaypoint = params.waypoints.find { waypoint ->
      val waypointLocation = Location("").apply {
        latitude = waypoint.position.latitude
        longitude = waypoint.position.longitude
      }

      val distanceInMeters = params.currentLocation.distanceTo(waypointLocation)
      distanceInMeters <= params.waypointRadiusMeters
    }

    FindWaypointFromUserLocationUC.Result(
      accuracy = accuracy,
      foundWaypoint = foundWaypoint,
    )
  }
}

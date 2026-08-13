package pl.dev.bkwiatkowski.feature.event.domain.usecase

import android.location.Location
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint
import kotlin.math.round

interface FindWaypointFromUserLocationUC : EitherUseCase<FindWaypointFromUserLocationUC.Params, MapWaypoint?> {
  data class Params(
    val currentLocation: Location,
    val waypoints: List<MapWaypoint>,
  ) : UseCase.Params
}

class FindWaypointFromUserLocationUCImpl : FindWaypointFromUserLocationUC {
  override suspend fun invoke(params: FindWaypointFromUserLocationUC.Params): Either<DomainError, MapWaypoint?> = either {
    fun Double.round4(): Double = round(this * 10000.0) / 10000.0

    val roundedUserLat = params.currentLocation.latitude.round4()
    val roundedUserLng = params.currentLocation.longitude.round4()

    params.waypoints.find { waypoint ->
      val wpLat = waypoint.position.latitude.round4()
      val wpLng = waypoint.position.longitude.round4()

      return@find (wpLat == roundedUserLat && wpLng == roundedUserLng)
    }
  }
}

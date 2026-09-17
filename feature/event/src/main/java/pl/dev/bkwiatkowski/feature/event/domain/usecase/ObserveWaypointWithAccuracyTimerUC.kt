package pl.dev.bkwiatkowski.feature.event.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.dev.bkwiatkowski.common.core.localization.GpsManager
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.time.TimeProvider
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint

interface ObserveWaypointWithAccuracyTimerUC {
  suspend operator fun invoke(
    waypoints: List<MapWaypoint>,
    waypointRadiusMeters: Float,
  ): Flow<Result>

  sealed interface Result {
    data class StrongAccuracyWithWaypoint(
      val waypoint: MapWaypoint,
    ) : Result

    data object StrongAccuracyNoWaypoint : Result

    data class WeakAccuracyWithWaypoint(
      val timerExpired: Boolean = false,
    ) : Result

    data class WeakAccuracyNoWaypoint(
      val timerExpired: Boolean = false,
    ) : Result

    data object VeryWeakAccuracy : Result
  }
}

class ObserveWaypointWithAccuracyTimerUCImpl(
  private val gpsManager: GpsManager,
  private val findWaypointFromUserLocationUC: FindWaypointFromUserLocationUC,
  private val timeProvider: TimeProvider,
) : ObserveWaypointWithAccuracyTimerUC {

  companion object {
    const val WEAK_ACCURACY_TIMEOUT_MILLIS = 20_000L
    const val ACCURACY_THRESHOLD = 50f
    const val VERY_WEAK_THRESHOLD = 150f
  }

  override suspend operator fun invoke(
    waypoints: List<MapWaypoint>,
    waypointRadiusMeters: Float,
  ): Flow<ObserveWaypointWithAccuracyTimerUC.Result> = flow {
    var lastWeakAccuracyTime: Long? = null
    var timerExpired = false

    gpsManager.getLocationFlow().collect { location ->
      val currentTime = timeProvider.currentTimeMillis()

      val result = findWaypointFromUserLocationUC(
        params = FindWaypointFromUserLocationUC.Params(
          currentLocation = location,
          waypoints = waypoints,
          waypointRadiusMeters = waypointRadiusMeters,
        )
      ).getRightOrNull()

      if (result != null) {
        val accuracy = result.accuracy
        val foundWaypoint = result.foundWaypoint

        val isStrongAccuracy = accuracy > 0 && accuracy <= ACCURACY_THRESHOLD
        val isWeakAccuracy = accuracy > ACCURACY_THRESHOLD && accuracy <= VERY_WEAK_THRESHOLD
        val isVeryWeakAccuracy = accuracy > VERY_WEAK_THRESHOLD

        if (lastWeakAccuracyTime != null) {
          val elapsedTime = currentTime - lastWeakAccuracyTime!!

          if (isStrongAccuracy) {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Weak accuracy timer cancelled - accuracy improved to strong"
            )
            lastWeakAccuracyTime = null
            timerExpired = false
          } else if (elapsedTime >= WEAK_ACCURACY_TIMEOUT_MILLIS && !timerExpired) {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Weak accuracy timer expired - setting timerExpired flag"
            )
            timerExpired = true
          } else if (isVeryWeakAccuracy) {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Very weak accuracy detected - cancelling weak accuracy timer"
            )
            lastWeakAccuracyTime = null
            timerExpired = false
          }
        }

        when {
          isVeryWeakAccuracy -> {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Very weak accuracy - Unreliable location"
            )
            lastWeakAccuracyTime = null
            timerExpired = false
            emit(value = ObserveWaypointWithAccuracyTimerUC.Result.VeryWeakAccuracy)
          }
          isStrongAccuracy && foundWaypoint != null -> {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Strong accuracy - Waypoint found: ${foundWaypoint.label}"
            )
            lastWeakAccuracyTime = null
            timerExpired = false
            emit(value = ObserveWaypointWithAccuracyTimerUC.Result.StrongAccuracyWithWaypoint(waypoint = foundWaypoint))
          }
          isStrongAccuracy && foundWaypoint == null -> {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Strong accuracy - No waypoint in range"
            )
            lastWeakAccuracyTime = null
            timerExpired = false
            emit(value = ObserveWaypointWithAccuracyTimerUC.Result.StrongAccuracyNoWaypoint)
          }
          isWeakAccuracy && foundWaypoint != null -> {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Weak accuracy - Waypoint found: ${foundWaypoint.label}, timerExpired=$timerExpired"
            )
            if (lastWeakAccuracyTime == null) {
              lastWeakAccuracyTime = currentTime
              timerExpired = false
              Log.i(
                tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
                message = "Weak accuracy timer started - waiting 20 seconds..."
              )
            }
            emit(
              value = ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint(
                timerExpired = timerExpired,
              ),
            )
          }
          else -> {
            Log.i(
              tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
              message = "Weak accuracy - No waypoint in range, timerExpired=$timerExpired"
            )
            if (lastWeakAccuracyTime == null) {
              lastWeakAccuracyTime = currentTime
              timerExpired = false
              Log.i(
                tag = Tag(this@ObserveWaypointWithAccuracyTimerUCImpl),
                message = "Weak accuracy timer started - waiting 20 seconds..."
              )
            }
            emit(value =ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyNoWaypoint(timerExpired = timerExpired))
          }
        }
      }
    }
  }
}

package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointsVisitedResponse
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository

interface GetSessionWaypointsUC : EitherUseCase<GetSessionWaypointsUC.Params, WaypointsVisitedResponse> {
  data class Params(
    val sessionUuid: String,
  ) : UseCase.Params
}

class GetSessionWaypointsUCImpl(
  private val eventRepository: EventRepository,
  private val eventBackendInteractor: EventBackendInteractor,
) : GetSessionWaypointsUC {
  override suspend fun invoke(params: GetSessionWaypointsUC.Params): Either<DomainError, WaypointsVisitedResponse> = either {
    val localVisits = eventRepository.getAllVisitsForSession(params.sessionUuid).getRight()

    eventBackendInteractor.getSessionWaypoints(sessionUuid = params.sessionUuid).fold(
      onLeft = { error ->
        when (error) {
          is DomainError.NoNetwork,
          is DomainError.UnavailableServer -> {
            WaypointsVisitedResponse(
              waypoints = localVisits.map { eventWaypoint ->
                SessionWaypointDetail(
                  waypointId = eventWaypoint.waypointId,
                  visitedAt = eventWaypoint.visitedAt,
                )
              }
            )
          }
          else -> raise(error = error)
        }
      },
      onRight = { backendWaypoints ->
        val localVisitIds = localVisits.map { it.waypointId }.toSet()

        val mergedWaypoints = buildList {
          for (localVisit in localVisits) {
            add(
              SessionWaypointDetail(
                waypointId = localVisit.waypointId,
                visitedAt = localVisit.visitedAt,
              )
            )
          }

          for (backendWaypoint in backendWaypoints.waypoints) {
            if (backendWaypoint.waypointId !in localVisitIds) {
              add(backendWaypoint)
            }
          }
        }

        WaypointsVisitedResponse(waypoints = mergedWaypoints)
      }
    )
  }
}

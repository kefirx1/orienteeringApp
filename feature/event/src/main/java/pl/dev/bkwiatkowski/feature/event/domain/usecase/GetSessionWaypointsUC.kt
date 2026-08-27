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
    eventBackendInteractor.getSessionWaypoints(sessionUuid = params.sessionUuid).fold(
      onLeft = { error ->
        when (error) {
          is DomainError.NoNetwork -> {
            val visits = eventRepository.getAllVisitsForSession(params.sessionUuid).getRight()
            WaypointsVisitedResponse(
              waypoints = visits.map { eventWaypoint ->
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
      onRight = { waypoints -> waypoints }
    )
  }
}

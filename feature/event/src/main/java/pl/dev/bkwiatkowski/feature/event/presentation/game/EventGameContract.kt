package pl.dev.bkwiatkowski.feature.event.presentation.game

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail

interface EventGameContract {
  suspend fun getEventDetails(): Either<DomainError, MobileEventDetails>
  fun getVisitedWaypoints(): List<SessionWaypointDetail>
  fun visitedWaypointsMonitor(): Flow<List<SessionWaypointDetail>>
}
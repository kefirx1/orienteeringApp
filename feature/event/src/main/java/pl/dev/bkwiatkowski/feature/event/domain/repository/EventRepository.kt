package pl.dev.bkwiatkowski.feature.event.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.EventWaypointVisitRecord
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import java.time.LocalDateTime

interface EventRepository {

  suspend fun saveWaypointVisit(
    waypointId: Int,
    visitedAt: LocalDateTime,
    imageBytes: ByteArray,
    sessionUuid: String,
  ): Either<DomainError, Unit>

  suspend fun finishSession(
    sessionUuid: String,
    eventId: Int,
  ): Either<DomainError, Unit>

  suspend fun getAllVisitsForSession(sessionUuid: String): Either<DomainError, List<EventWaypointVisitRecord>>

  suspend fun markVisitAsSent(waypointId: Int, sessionUuid: String): Either<DomainError, Unit>

  fun observeLocalVisits(): Flow<WaypointVisitResponse>

  suspend fun publishWaypointVisit(
    waypointId: Int,
    visitedAt: LocalDateTime,
  ): Either<DomainError, Unit>

  suspend fun getUnsentVisitsForSession(sessionUuid: String): Either<DomainError, List<EventWaypointVisitRecord>>

  suspend fun readImageBytes(path: String): Either<DomainError, ByteArray>

  suspend fun saveEventDetails(eventDetails: MobileEventDetails): Either<DomainError, Unit>

  suspend fun getEventDetails(eventId: Int): Either<DomainError, MobileEventDetails>

  suspend fun clearEventDetails(eventId: Int): Either<DomainError, Unit>

  suspend fun getLastSavedEventDetails(): Either<DomainError, MobileEventDetails>
}
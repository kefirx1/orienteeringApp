package pl.dev.bkwiatkowski.feature.maps.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEvents
import pl.dev.bkwiatkowski.feature.maps.domain.model.UserSessionStatus
import pl.dev.bkwiatkowski.feature.maps.domain.model.SessionParticipant

interface MapsBackendInteractor {
  suspend fun getMobileEvents(): Either<DomainError, MobileEvents>
  suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetails>
  suspend fun joinEventSession(sessionUuid: String): Either<DomainError, Unit>
  suspend fun checkUserInEventSession(sessionUuid: String): Either<DomainError, UserSessionStatus>
  suspend fun getSessionParticipantForUser(sessionUuid: String): Either<DomainError, SessionParticipant>
}
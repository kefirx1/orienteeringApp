package pl.dev.bkwiatkowski.technical.backend.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEFinishSessionResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BESessionWaypointDetailsResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUploadImageResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSessionStatus
import pl.dev.bkwiatkowski.technical.backend.domain.model.BESessionParticipant
import pl.dev.bkwiatkowski.technical.backend.domain.model.WebsocketWaypointVisit

interface BackendEventsRepository {
  suspend fun getMobileEvents(): Either<DomainError, List<MobileEventListResponse>>
  suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetailResponse>
  suspend fun joinEventSession(sessionUuid: String): Either<DomainError, Unit>
  suspend fun checkUserInEventSession(sessionUuid: String): Either<DomainError, BEUserSessionStatus>
  suspend fun uploadSessionImage(sessionUuid: String, imageBase64: String): Either<DomainError, BEUploadImageResponse>
  suspend fun postSessionWaypointVisits(sessionUuid: String, visits: List<WebsocketWaypointVisit>): Either<DomainError, Unit>
  suspend fun getWaypointsVisited(sessionUuid: String): Either<DomainError, BESessionWaypointDetailsResponse>
  suspend fun finishEventSession(sessionUuid: String): Either<DomainError, BEFinishSessionResponse>
  suspend fun getFinishedSessionParticipantsForUser(sessionUuid: String): Either<DomainError, List<BESessionParticipant>>
}
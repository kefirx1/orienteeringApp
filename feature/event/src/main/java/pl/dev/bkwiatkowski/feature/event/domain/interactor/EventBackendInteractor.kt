package pl.dev.bkwiatkowski.feature.event.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.UploadImageResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointsVisitedResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.WebsocketWaypointVisit

interface EventBackendInteractor {
  suspend fun uploadSessionImage(sessionUuid: String, imageBase64: String): Either<DomainError, UploadImageResponse>
  suspend fun postSessionWaypointVisit(sessionUuid: String, visit: WebsocketWaypointVisit): Either<DomainError, WaypointVisitResponse>
  suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetails>
  suspend fun getSessionWaypoints(sessionUuid: String): Either<DomainError, WaypointsVisitedResponse>
  suspend fun postSessionWaypointVisits(sessionUuid: String, visits: List<WebsocketWaypointVisit>): Either<DomainError, Unit>
  suspend fun finishEventSession(sessionUuid: String): Either<DomainError, FinishSessionResponse>
}
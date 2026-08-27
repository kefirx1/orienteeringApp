package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.feature.event.domain.model.WebsocketWaypointVisit
import javax.inject.Inject

interface FinishSessionUC : EitherUseCase<FinishSessionUC.Params, FinishSessionResponse> {
  data class Params(
    val sessionUuid: String,
    val eventId: Int,
  ) : UseCase.Params
}

class FinishSessionUCImpl @Inject constructor(
  private val eventRepository: EventRepository,
  private val eventBackendInteractor: EventBackendInteractor,
  private val base64Coder: Base64Coder,
) : FinishSessionUC {

  override suspend fun invoke(params: FinishSessionUC.Params): Either<DomainError, FinishSessionResponse> = either {
    val unsent = eventRepository.getUnsentVisitsForSession(sessionUuid = params.sessionUuid).getRight()

    val visitsToPost = unsent.map { visit ->
      val bytes = eventRepository.readImageBytes(path = visit.imagePath).getRight()

      val imageBase64 = base64Coder.encode(data = bytes).getRight()
      val uploadResponse = eventBackendInteractor.uploadSessionImage(
        sessionUuid = params.sessionUuid,
        imageBase64 = imageBase64,
      ).getRight()

      WebsocketWaypointVisit(
        waypointId = visit.waypointId,
        visitedAt = visit.visitedAt,
        imagePath = uploadResponse.path,
      )
    }

    if (visitsToPost.isNotEmpty()) {
      eventBackendInteractor.postSessionWaypointVisits(
        sessionUuid = params.sessionUuid,
        visits = visitsToPost,
      ).getRight()

      unsent.forEach { visit ->
        eventRepository.markVisitAsSent(
          waypointId = visit.waypointId,
          sessionUuid = params.sessionUuid,
        ).getRight()
      }
    }

    val response = eventBackendInteractor.finishEventSession(sessionUuid = params.sessionUuid).getRight()

    eventRepository.finishSession(
      sessionUuid = params.sessionUuid,
      eventId = params.eventId,
    ).getRight()

    response
  }
}
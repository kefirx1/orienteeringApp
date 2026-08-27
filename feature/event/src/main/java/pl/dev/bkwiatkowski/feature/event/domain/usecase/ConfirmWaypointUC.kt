package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.camera.domain.usecase.TakePictureAndCompressUC
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import java.time.LocalDateTime

interface ConfirmWaypointUC : EitherUseCase<ConfirmWaypointUC.Params, ConfirmWaypointUC.Result> {
  data class Params(
    val sessionUuid: String,
    val waypointId: Int,
  ) : UseCase.Params

  sealed interface Result {
    data object Success : Result
    data class BackendFailed(
      val visitedAt: LocalDateTime,
    ) : Result
  }
}

class ConfirmWaypointUCImpl(
  private val takePictureAndCompressUC: TakePictureAndCompressUC,
  private val eventRepository: EventRepository,
  private val eventBackendInteractor: EventBackendInteractor,
  private val base64Coder: Base64Coder,
) : ConfirmWaypointUC {

  override suspend fun invoke(params: ConfirmWaypointUC.Params): Either<DomainError, ConfirmWaypointUC.Result> = either {
    val visitedAt = LocalDateTime.now()
    val bytes = takePictureAndCompressUC(params = UseCase.Params.Empty).getRight()

    eventRepository.saveWaypointVisit(
      waypointId = params.waypointId,
      visitedAt = visitedAt,
      imageBytes = bytes,
      sessionUuid = params.sessionUuid,
    ).getRight()

    val uploadResponse = eventBackendInteractor.uploadSessionImage(
      sessionUuid = params.sessionUuid,
      imageBase64 = base64Coder.encode(data = bytes).getRight(),
    ).getRightOrElse {
      return@either ConfirmWaypointUC.Result.BackendFailed(
        visitedAt = visitedAt,
      )
    }

    eventBackendInteractor.confirmWaypoint(
      waypointId = params.waypointId,
      visitedAt = visitedAt,
      imagePath = uploadResponse.path,
    ).getRightOrElse {
      return@either ConfirmWaypointUC.Result.BackendFailed(
        visitedAt = visitedAt,
      )
    }

    eventRepository.markVisitAsOnline(
      waypointId = params.waypointId,
      sessionUuid = params.sessionUuid,
    ).getRight()

    ConfirmWaypointUC.Result.Success
  }
}

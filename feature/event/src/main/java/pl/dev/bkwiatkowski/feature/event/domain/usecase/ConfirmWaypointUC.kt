package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.camera.domain.usecase.TakePictureAndCompressUC
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.WebsocketWaypointVisit
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import java.time.LocalDateTime

interface ConfirmWaypointUC : EitherUseCase<ConfirmWaypointUC.Params, ConfirmWaypointUC.Result> {
  data class Params(
    val sessionUuid: String,
    val waypointId: Int,
    val maxImageSizeBytes: Int,
    val compressedImageQualityPercent: Int,
  ) : UseCase.Params

  data class Result(
    val visitedAt: LocalDateTime,
  )
}

class ConfirmWaypointUCImpl(
  private val takePictureAndCompressUC: TakePictureAndCompressUC,
  private val eventRepository: EventRepository,
  private val eventBackendInteractor: EventBackendInteractor,
  private val base64Coder: Base64Coder,
) : ConfirmWaypointUC {

  override suspend fun invoke(params: ConfirmWaypointUC.Params): Either<DomainError, ConfirmWaypointUC.Result> = either {
    val visitedAt = LocalDateTime.now()
    val bytes = takePictureAndCompressUC(
      params = TakePictureAndCompressUC.Params(
        compressedQualityPercent = params.compressedImageQualityPercent,
        maxCompressedImageSizeBytes = params.maxImageSizeBytes,
      ),
    ).getRight()

    eventRepository.saveWaypointVisit(
      waypointId = params.waypointId,
      visitedAt = visitedAt,
      imageBytes = bytes,
      sessionUuid = params.sessionUuid,
    ).getRight()

    val uploadResponse = eventBackendInteractor.uploadSessionImage(
      sessionUuid = params.sessionUuid,
      imageBase64 = base64Coder.encode(data = bytes).getRight(),
    ).getRightOrElse { error ->
      if (error is DomainError.NoNetwork) {
        return@either ConfirmWaypointUC.Result(
          visitedAt = visitedAt,
        )
      } else {
        raise(error = error)
      }
    }
    Log.i(
      tag = Tag(this@ConfirmWaypointUCImpl),
      message = "Uploaded image for waypointId: ${params.waypointId} at path: ${uploadResponse.path}",
    )

    val response = eventBackendInteractor.postSessionWaypointVisit(
      sessionUuid = params.sessionUuid,
      visit = WebsocketWaypointVisit(
        waypointId = params.waypointId,
        visitedAt = visitedAt,
        imagePath = uploadResponse.path,
      ),
    ).getRightOrElse {
      return@either ConfirmWaypointUC.Result(
        visitedAt = visitedAt,
      )
    }
    Log.i(
      tag = Tag(this@ConfirmWaypointUCImpl),
      message = "Confirmed waypointId: ${params.waypointId} at visitedAt: $visitedAt",
    )

    eventRepository.markVisitAsSent(
      waypointId = params.waypointId,
      sessionUuid = params.sessionUuid,
    ).getRight()
    Log.i(
      tag = Tag(this@ConfirmWaypointUCImpl),
      message = "Marked visit as sent for waypointId: ${params.waypointId} at visitedAt: $visitedAt",
    )

    ConfirmWaypointUC.Result(
      visitedAt = response.lastVisitedWaypoint.visitedAt,
    )
  }
}

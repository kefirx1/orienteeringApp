package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import java.time.LocalDateTime

interface PublishWaypointVisitUC : EitherUseCase<PublishWaypointVisitUC.Params, Unit> {
  data class Params(
    val waypointId: Int,
    val visitedAt: LocalDateTime,
  ) : UseCase.Params
}

class PublishWaypointVisitUCImpl(
  private val eventRepository: EventRepository,
) : PublishWaypointVisitUC {
  override suspend fun invoke(params: PublishWaypointVisitUC.Params): Either<DomainError, Unit> =
    eventRepository.publishWaypointVisit(
      waypointId = params.waypointId,
      visitedAt = params.visitedAt,
    )
}

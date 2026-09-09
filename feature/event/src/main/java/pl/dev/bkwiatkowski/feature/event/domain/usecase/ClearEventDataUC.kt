package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository

interface ClearEventDataUC : EitherUseCase<UseCase.Params.Empty, Unit>

class ClearEventDataUCImpl(
  private val eventRepository: EventRepository,
): ClearEventDataUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, Unit> =
    eventRepository.clearAllEventData()
}
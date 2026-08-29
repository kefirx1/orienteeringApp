package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository

interface GetLastActiveSavedEventUC : EitherUseCase<UseCase.Params.Empty, MobileEventDetails>

class GetLastActiveSavedEventUCImpl(
  private val eventRepository: EventRepository,
) : GetLastActiveSavedEventUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, MobileEventDetails> =
    eventRepository.getLastSavedEventDetails()
}
package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository

interface GetLastActiveSavedEventUC : EitherUseCase<UseCase.Params.Empty, MobileEventDetails>

class GetLastActiveSavedEventUCImpl(
  private val eventRepository: EventRepository,
) : GetLastActiveSavedEventUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, MobileEventDetails> = either {
    val details = eventRepository.getLastSavedEventDetails().getRightOrNull()
      ?: raise(error = DomainError.Custom(NullPointerException("There is no saved details")))

    val unsent = eventRepository.getUnsentVisitsForSession(sessionUuid = details.session.id).getRightOrNull()
    if (!unsent.isNullOrEmpty()) details else
      raise(error = DomainError.Custom(NullPointerException("No unsent visits for ${details.session.id} session")))
  }
}
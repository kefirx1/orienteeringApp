package pl.dev.bkwiatkowski.feature.event.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository

interface GetEventDetailsUC : EitherUseCase<GetEventDetailsUC.Params, MobileEventDetails> {
  data class Params(
    val eventId: Int,
  ) : UseCase.Params
}

class GetEventDetailsUCImpl(
  private val eventRepository: EventRepository,
  private val eventBackendInteractor: EventBackendInteractor,
) : GetEventDetailsUC {
  override suspend fun invoke(params: GetEventDetailsUC.Params): Either<DomainError, MobileEventDetails> = either {
    eventBackendInteractor.getMobileEventDetails(eventId = params.eventId).fold(
      onLeft = { error ->
        when (error) {
          is DomainError.NoNetwork -> eventRepository.getEventDetails(params.eventId).getRight()
          else -> raise(error = error)
        }
      },
      onRight = { details ->
        eventRepository.saveEventDetails(details).getRightOrNull()
        details
      }
    )
  }
}

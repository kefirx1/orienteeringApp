package pl.dev.bkwiatkowski.feature.event.presentation.game

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails

interface EventGameContract {
  suspend fun getEventDetails(): Either<DomainError, MobileEventDetails>
}
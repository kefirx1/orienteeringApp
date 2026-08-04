package pl.dev.bkwiatkowski.feature.event.presentation.map

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails

interface EventMapContract {
  suspend fun getEventDetails(): Either<DomainError, MobileEventDetails>
}
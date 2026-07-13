package pl.dev.bkwiatkowski.feature.maps.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEvents

interface MapsBackendInteractor {
  suspend fun getMobileEvents(): Either<DomainError, MobileEvents>
  suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetails>
}
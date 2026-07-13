package pl.dev.bkwiatkowski.technical.backend.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventListResponse

interface BackendEventsRepository {
  suspend fun getMobileEvents(): Either<DomainError, List<MobileEventListResponse>>
  suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetailResponse>
}
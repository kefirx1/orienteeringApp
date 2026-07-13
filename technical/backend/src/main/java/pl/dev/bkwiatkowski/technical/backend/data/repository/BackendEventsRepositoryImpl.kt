package pl.dev.bkwiatkowski.technical.backend.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.api.GetMobileEventById
import pl.dev.bkwiatkowski.technical.backend.api.GetMobileEvents
import pl.dev.bkwiatkowski.technical.backend.data.MobileEventListResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileEventDetailResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendEventsRepository

class BackendEventsRepositoryImpl(
  private val callMediator: CallMediator,
  private val clientFactory: HttpClientFactory,
) : BackendEventsRepository {
  private val client by lazy {
    clientFactory.create()
  }

  override suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetailResponse> =
    callMediator<GetMobileEventById> {
      client.get(resource = GetMobileEventById(id = eventId)).body()
    }.mapRight { response -> response.body<MobileEventDetailResponseDto>().toDomain() }

  override suspend fun getMobileEvents(): Either<DomainError, List<MobileEventListResponse>> =
    callMediator<GetMobileEvents> {
      client.get(resource = GetMobileEvents).body()
    }.mapRight { response ->
      response
        .body<List<MobileEventListResponseDto>>()
        .map { it.toDomain() }
    }
}
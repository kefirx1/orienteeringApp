package pl.dev.bkwiatkowski.technical.backend.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.api.GetMobileSettings
import pl.dev.bkwiatkowski.technical.backend.data.MobileSettingsResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSettingsResponse
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendSettingsRepository

class BackendSettingsRepositoryImpl(
  private val callMediator: CallMediator,
  private val clientFactory: HttpClientFactory,
) : BackendSettingsRepository {

  private val client by lazy {
    clientFactory.create()
  }

  override suspend fun getMobileSettings(): Either<DomainError, MobileSettingsResponse> =
    callMediator<GetMobileSettings> {
      client.get(resource = GetMobileSettings).body()
    }.mapRight { response -> response.body<MobileSettingsResponseDto>().toDomain() }
}

package pl.dev.bkwiatkowski.technical.mobile.data.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.mobile.domain.model.MobileSettings
import pl.dev.bkwiatkowski.technical.mobile.domain.repository.MobileSettingsRepository

class MobileSettingsRepositoryImpl : MobileSettingsRepository {
  private var settings: MobileSettings? = null

  override suspend fun getMobileSettings(): Either<DomainError, MobileSettings> = either {
    settings ?: raise(error = DomainError.Custom(NullPointerException("Mobile settings are null")))
  }

  override suspend fun saveMobileSettings(settings: MobileSettings): Either<DomainError, Unit> = either {
    this@MobileSettingsRepositoryImpl.settings = settings
  }
}
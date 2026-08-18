package pl.dev.bkwiatkowski.technical.user.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.user.domain.model.UserSettings

interface UserRepository {
  suspend fun saveNewUserSettings(userSettings: UserSettings) : Either<DomainError, Unit>
  suspend fun getUserSettings(): Either<DomainError, UserSettings>
  suspend fun clearUserSettings(): Either<DomainError, Unit>
}
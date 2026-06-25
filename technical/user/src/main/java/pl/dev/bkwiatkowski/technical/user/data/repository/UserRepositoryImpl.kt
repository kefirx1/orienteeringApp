package pl.dev.bkwiatkowski.technical.user.data.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.user.data.mapper.UserMapper.toDomain
import pl.dev.bkwiatkowski.technical.user.data.mapper.UserMapper.toDto
import pl.dev.bkwiatkowski.technical.user.data.model.UserSettingsDto
import pl.dev.bkwiatkowski.technical.user.domain.model.UserSettings
import pl.dev.bkwiatkowski.technical.user.domain.repository.UserRepository

class UserRepositoryImpl(
  private val dataStoreProvider: DataStoreProvider,
) : UserRepository {
  companion object {
    private const val USER_SETTINGS_STORE_NAME = "USER_SETTINGS"
  }

  override suspend fun saveNewUserSettings(userSettings: UserSettings) =
    dataStoreProvider.updateDataStoreData(
      dataStoreKey = USER_SETTINGS_STORE_NAME,
      data = userSettings.toDto(),
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.AppSecretKey,
    )

  override suspend fun getUserSettings(): Either<DomainError, UserSettings> =
    dataStoreProvider.getDataStoreData<UserSettingsDto>(
      dataStoreKey = USER_SETTINGS_STORE_NAME,
      type = UserSettingsDto::class.java,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.AppSecretKey,
    ).mapRight { it.toDomain() }
}
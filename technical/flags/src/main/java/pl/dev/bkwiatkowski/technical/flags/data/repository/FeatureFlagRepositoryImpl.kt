package pl.dev.bkwiatkowski.technical.flags.data.repository

import pl.dev.bkwiatkowski.common.core.config.EnvironmentConfig
import pl.dev.bkwiatkowski.common.core.config.Flavor
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.technical.flags.domain.model.FeatureFlag
import pl.dev.bkwiatkowski.technical.flags.domain.repository.FeatureFlagRepository

class FeatureFlagRepositoryImpl(
  private val dataStoreProvider: DataStoreProvider,
  private val environmentConfig: EnvironmentConfig,
) : FeatureFlagRepository {

  companion object {
    private const val FEATURE_FLAG_DATA_STORE_KEY = "FEATURE_FLAGS"
  }

  override suspend fun isFeatureFlagEnabled(flag: FeatureFlag): Either<DomainError, Boolean> = either {
    val dataStoreKey = "${FEATURE_FLAG_DATA_STORE_KEY}_${flag.value}"

    dataStoreProvider.getDataStoreData<Boolean>(
      dataStoreKey = dataStoreKey,
      type = Boolean::class.java,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.AppSecretKey,
    ).onLeft {
      return@either getDefaultValue(flag)
    }.getRight()
  }

  override suspend fun setFeatureFlagValue(flag: FeatureFlag, enabled: Boolean): Either<DomainError, Unit> = either {
    val dataStoreKey = "${FEATURE_FLAG_DATA_STORE_KEY}_${flag.value}"

    dataStoreProvider.updateDataStoreData(
      dataStoreKey = dataStoreKey,
      data = enabled,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.AppSecretKey,
    ).getRight()
  }

  private fun getDefaultValue(flag: FeatureFlag): Boolean {
    return when (environmentConfig.flavor) {
      Flavor.DEVELOP -> flag.debug
      Flavor.PROD -> flag.prod
    }
  }
}

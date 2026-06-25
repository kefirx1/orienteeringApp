package pl.dev.bkwiatkowski.common.core.storage.provider

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import java.lang.reflect.Type

interface DataStoreProvider {
  sealed interface DataStoreKeyProvider {
    data object AppSecretKey : DataStoreKeyProvider
    data object MasterKey : DataStoreKeyProvider
  }

  suspend fun <T> getDataStoreData(
    dataStoreKey: String,
    type: Type,
    dataStoreKeyProvider: DataStoreKeyProvider,
  ): Either<DomainError, T>

  suspend fun <T> getDataStoreDataFlow(
    dataStoreKey: String,
    type: Type,
    dataStoreKeyProvider: DataStoreKeyProvider,
  ): Either<DomainError, Flow<T>>

  suspend fun <T> updateDataStoreData(
    dataStoreKey: String,
    data: T,
    dataStoreKeyProvider: DataStoreKeyProvider,
  ): Either<DomainError, Unit>

  suspend fun clearDataStoreData(
    dataStoreKey: String,
  ): Either<DomainError, Unit>
}
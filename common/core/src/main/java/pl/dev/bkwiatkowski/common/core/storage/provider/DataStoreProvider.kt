package pl.dev.bkwiatkowski.common.core.storage.provider

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import java.lang.reflect.Type

interface DataStoreProvider {
  suspend fun <T> getDataStoreData(dataStoreKey: String, type: Type): Either<DomainError, T>

  suspend fun <T> getDataStoreDataFlow(dataStoreKey: String, type: Type): Either<DomainError, Flow<T>>

  suspend fun <T> updateDataStoreData(dataStoreKey: String, data: T): Either<DomainError, Unit>
}
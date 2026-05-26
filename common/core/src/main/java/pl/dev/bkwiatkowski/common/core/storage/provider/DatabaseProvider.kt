package pl.dev.bkwiatkowski.common.core.storage.provider

import androidx.room.RoomDatabase
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import javax.crypto.SecretKey

interface DatabaseProvider {
  fun <T : RoomDatabase> getDatabase(
    databaseName: String,
    databaseClass: Class<T>,
    masterKey: SecretKey,
    typeConverters: List<Any> = listOf(),
  ): Either<DomainError, T>
}
package pl.dev.bkwiatkowski.common.storage.provider

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.storage.provider.DatabaseProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import javax.crypto.SecretKey

class DatabaseProviderImpl(
  private val context: Context,
): DatabaseProvider {
  private val databaseCache: MutableMap<String, RoomDatabase> = mutableMapOf()

  override fun <T : RoomDatabase> getDatabase(
    databaseName: String,
    databaseClass: Class<T>,
    masterKey: SecretKey,
    typeConverters: List<Any>,
  ): Either<DomainError, T> = either {
    getCachedDatabase<T>(databaseName = databaseName).onRight { database ->
      return@either database
    }

    val factory = SupportFactory(masterKey.encoded)

    val database = Room.databaseBuilder(
      context.applicationContext,
      databaseClass,
      databaseName,
    ).openHelperFactory(factory)
      .apply {
        typeConverters.forEach { converter ->
          addTypeConverter(converter)
        }
      }
      .build()

    databaseCache[databaseName] = database
    database
  }

  private fun <T> getCachedDatabase(databaseName: String): Either<DomainError, T> = either {
    val cachedDatabase = databaseCache[databaseName]
    if (cachedDatabase != null && cachedDatabase.isOpen) {
      return@either cachedDatabase as T
    }

    raise(
      error = DomainError.Custom(NullPointerException("Database $databaseName is not available"))
    )
  }
}
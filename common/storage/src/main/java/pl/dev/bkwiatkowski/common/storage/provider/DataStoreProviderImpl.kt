package pl.dev.bkwiatkowski.common.storage.provider

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.provider.AppSecretKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.Base64Coder
import pl.dev.bkwiatkowski.common.core.storage.JsonSerializer
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import java.lang.reflect.Type

class DataStoreProviderImpl(
  private val cryptoManager: CryptoManager,
  private val context: Context,
  private val jsonSerializer: JsonSerializer,
  private val base64Coder: Base64Coder,
  private val appSecretKeyProvider: AppSecretKeyProvider,
): DataStoreProvider {

  companion object {
    private const val APP_DATA_STORE_PREFS_NAME = "app_data_store_prefs"
  }

  val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_DATA_STORE_PREFS_NAME)

  override suspend fun <T> getDataStoreData(dataStoreKey: String, type: Type): Either<DomainError, T> = either {
    getDataStore().data.firstOrNull()?.let { prefs ->
      val data = (prefs.get(key = stringPreferencesKey(dataStoreKey)) ?: raise(
        error = DomainError.Custom(NullPointerException("Data for key $dataStoreKey not found in DataStore"))
      ))

      val decodedData = base64Coder.decode(data = data).getRight()
      val decryptedData = cryptoManager.decryptData(
        data = decodedData,
        cryptography = Cryptography.AES_CBC_PKCS7,
        key = appSecretKeyProvider.getKeyStoreSecretKey(cryptography = Cryptography.AES_CBC_PKCS7).getRight(),
      )?.decodeToString()

      jsonSerializer.deserialize<T>(serializedData = decryptedData, type = type).getRight()

    } ?: raise(
      error = DomainError.Custom(NullPointerException("DataStore is not initialized or empty"))
    )
  }

  override suspend fun <T> getDataStoreDataFlow(
    dataStoreKey: String,
    type: Type,
  ): Either<DomainError, Flow<T>> = either {
    getDataStore().data.mapNotNull { prefs ->
      val data = (prefs.get(key = stringPreferencesKey(dataStoreKey)) ?: return@mapNotNull null)

      val decodedData = base64Coder.decode(data = data).getRight()
      val decryptedData = cryptoManager.decryptData(
        data = decodedData,
        cryptography = Cryptography.AES_CBC_PKCS7,
        key = appSecretKeyProvider.getKeyStoreSecretKey(cryptography = Cryptography.AES_CBC_PKCS7).getRight(),
      )?.decodeToString()

      jsonSerializer.deserialize<T>(serializedData = decryptedData, type = type).getRight()
    }
  }

  override suspend fun <T> updateDataStoreData(
    dataStoreKey: String,
    data: T
  ) = either {
    val encryptedData = cryptoManager.encryptData(
      data = jsonSerializer.serialize(data = data).getRight().toByteArray(),
      cryptography = Cryptography.AES_CBC_PKCS7,
      key = appSecretKeyProvider.getKeyStoreSecretKey(cryptography = Cryptography.AES_CBC_PKCS7).getRight(),
    )

    getDataStore().edit { prefs ->
      prefs[stringPreferencesKey(name = dataStoreKey)] =
        base64Coder.encode(data = encryptedData).getRight()
    }

    Unit
  }

  private fun getDataStore(): DataStore<Preferences> = context.dataStore
}
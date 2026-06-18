package pl.dev.bkwiatkowski.common.security.provider

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.security.MasterKeyDataStore
import javax.crypto.SecretKey

class MasterKeyProviderImpl(
  private val masterKeyDataStore: MasterKeyDataStore,
): MasterKeyProvider {
  override fun saveDecryptedMasterKey(masterKey: SecretKey): Either<DomainError, Unit> = either {
    masterKeyDataStore.saveKey(secretKey = masterKey)
  }

  override fun getMasterKey(): Either<DomainError, SecretKey> = either {
    masterKeyDataStore.getKey()
      ?: raise(error = DomainError.Custom(NullPointerException("Master key not found")))
  }

  override fun clearCachedKey() {
    masterKeyDataStore.clearKey()
  }
}
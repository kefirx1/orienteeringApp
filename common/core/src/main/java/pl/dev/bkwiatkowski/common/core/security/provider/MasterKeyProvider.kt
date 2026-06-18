package pl.dev.bkwiatkowski.common.core.security.provider

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import javax.crypto.SecretKey

interface MasterKeyProvider {
  fun getMasterKey(): Either<DomainError, SecretKey>

  fun saveDecryptedMasterKey(masterKey: SecretKey): Either<DomainError, Unit>

  fun clearCachedKey()
}
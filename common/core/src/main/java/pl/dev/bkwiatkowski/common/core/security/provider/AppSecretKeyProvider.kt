package pl.dev.bkwiatkowski.common.core.security.provider

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.usecase.Either
import javax.crypto.SecretKey

interface AppSecretKeyProvider {
  fun getKeyStoreSecretKey(
    cryptography: Cryptography,
    authenticationRequired: Boolean = false,
    keyAlias: String? = null,
  ): Either<DomainError, SecretKey>
}
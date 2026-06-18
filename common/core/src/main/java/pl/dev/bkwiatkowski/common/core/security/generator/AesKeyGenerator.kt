package pl.dev.bkwiatkowski.common.core.security.generator

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.usecase.Either
import javax.crypto.SecretKey

interface AesKeyGenerator {
  fun generateSecretKeyFromBase(
    cryptography: Cryptography,
    base: CharArray,
    salt: ByteArray,
  ): Either<DomainError, SecretKey>

  fun generateKeyStoreKey(
    cryptography: Cryptography,
    authenticationRequired: Boolean,
    keyAlias: String,
    provider: String,
  ): Either<DomainError, SecretKey>

  fun generateMasterKey(): Either<DomainError, SecretKey>
}
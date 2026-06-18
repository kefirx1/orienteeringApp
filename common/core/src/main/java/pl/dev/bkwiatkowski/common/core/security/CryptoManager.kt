package pl.dev.bkwiatkowski.common.core.security

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import javax.crypto.Cipher
import javax.crypto.SecretKey

interface CryptoManager {
  fun getBaseEncryptCipher(
    cryptography: Cryptography,
    key: SecretKey,
  ): Either<DomainError, Cipher>

  fun getBaseDecryptCipher(
    cryptography: Cryptography,
    key: SecretKey,
    encryptedData: ByteArray,
  ): Either<DomainError, Cipher>

  fun encryptData(
    data: ByteArray,
    cryptography: Cryptography,
    key: SecretKey,
  ): Either<DomainError, ByteArray>

  fun decryptData(
    data: ByteArray,
    cryptography: Cryptography,
    key: SecretKey,
  ): Either<DomainError, ByteArray>

  fun encryptWithKey(
    data: ByteArray,
    key: SecretKey,
    initialCipher: Cipher? = null,
    cryptography: Cryptography,
  ): Either<DomainError, ByteArray>

  fun decryptWithKey(
    data: ByteArray,
    key: SecretKey,
    initialCipher: Cipher? = null,
    cryptography: Cryptography,
  ): Either<DomainError, ByteArray>

}
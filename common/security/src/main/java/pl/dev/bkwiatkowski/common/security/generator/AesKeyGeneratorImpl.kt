package pl.dev.bkwiatkowski.common.security.generator

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.Cryptography.Companion.DERIVED_KEY_ITERATIONS
import pl.dev.bkwiatkowski.common.core.security.Cryptography.Companion.DERIVED_KEY_LENGTH
import pl.dev.bkwiatkowski.common.core.security.Cryptography.Companion.KEY_DERIVATION_ALGORITHM
import pl.dev.bkwiatkowski.common.core.security.generator.AesKeyGenerator
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AesKeyGeneratorImpl : AesKeyGenerator {
  override fun generateSecretKeyFromBase(
    cryptography: Cryptography,
    base: CharArray,
    salt: ByteArray
  ): Either<DomainError, SecretKey> = either {
    val factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
    val spec = PBEKeySpec(base, salt, DERIVED_KEY_ITERATIONS, DERIVED_KEY_LENGTH)
    val key = factory.generateSecret(spec)
    SecretKeySpec(key.encoded, Cryptography.AES_GCM_NoPadding.algorithm)
  }

  override fun generateKeyStoreKey(
    cryptography: Cryptography,
    authenticationRequired: Boolean,
    keyAlias: String,
    provider: String,
  ): Either<DomainError, SecretKey> = either {
    KeyGenerator.getInstance(cryptography.algorithm, provider).apply {
      init(
        KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
          .setBlockModes(cryptography.blockMode)
          .setEncryptionPaddings(cryptography.padding)
          .setUserAuthenticationRequired(authenticationRequired)
          .setInvalidatedByBiometricEnrollment(true)
          .setRandomizedEncryptionRequired(true)
          .build()
      )
    }.generateKey()
  }

  override fun generateMasterKey(): Either<DomainError, SecretKey> = either {
    val key = ByteArray(Cryptography.AES_GCM_NoPadding.keySize / 8)
    SecureRandom().nextBytes(key)
    SecretKeySpec(key, Cryptography.AES_GCM_NoPadding.algorithm)
  }
}
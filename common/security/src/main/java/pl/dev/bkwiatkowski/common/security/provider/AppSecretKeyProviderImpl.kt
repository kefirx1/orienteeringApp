package pl.dev.bkwiatkowski.common.security.provider

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.generator.AesKeyGenerator
import pl.dev.bkwiatkowski.common.core.security.provider.AppSecretKeyProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import java.security.KeyStore
import javax.crypto.SecretKey

class AppSecretKeyProviderImpl(
  private val aesKeyGenerator: AesKeyGenerator,
) : AppSecretKeyProvider {

  companion object {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "keyAlias"
  }

  private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
    load(null)
  }

  override fun getKeyStoreSecretKey(
    cryptography: Cryptography,
    authenticationRequired: Boolean,
    keyAlias: String?,
  ): Either<DomainError, SecretKey> = either {
    val key = keyStore.getEntry(keyAlias ?: KEY_ALIAS, null) as? KeyStore.SecretKeyEntry

    key?.secretKey ?: aesKeyGenerator.generateKeyStoreKey(
      cryptography = cryptography,
      authenticationRequired = authenticationRequired,
      keyAlias = keyAlias ?: KEY_ALIAS,
      provider = ANDROID_KEYSTORE,
    ).getRight()
  }
}
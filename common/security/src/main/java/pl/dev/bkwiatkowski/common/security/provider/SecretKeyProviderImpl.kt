package pl.dev.bkwiatkowski.common.security.provider

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.Cryptography.Companion.DERIVED_KEY_ITERATIONS
import pl.dev.bkwiatkowski.common.core.security.Cryptography.Companion.DERIVED_KEY_LENGTH
import pl.dev.bkwiatkowski.common.core.security.Cryptography.Companion.KEY_DERIVATION_ALGORITHM
import pl.dev.bkwiatkowski.common.core.security.provider.SecretKeyProvider
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class SecretKeyProviderImpl(): SecretKeyProvider {

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
  ): SecretKey {
    val key = keyStore.getEntry(keyAlias ?: KEY_ALIAS, null) as? KeyStore.SecretKeyEntry

    return key?.secretKey ?: generateKeyStoreKey(
      cryptography = cryptography,
      authenticationRequired = authenticationRequired,
      keyAlias = keyAlias,
    )
  }

  override fun getSecretKeyFromBase(cryptography: Cryptography, base: CharArray, salt: ByteArray): SecretKey {
    val factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
    val spec = PBEKeySpec(base, salt, DERIVED_KEY_ITERATIONS, DERIVED_KEY_LENGTH)
    val key = factory.generateSecret(spec)
    return SecretKeySpec(key.encoded, Cryptography.AES_GCM_NoPadding.algorithm)
  }

  private fun generateKeyStoreKey(
    cryptography: Cryptography,
    authenticationRequired: Boolean,
    keyAlias: String?,
  ): SecretKey {
    return KeyGenerator.getInstance(cryptography.algorithm, ANDROID_KEYSTORE).apply {
      init(
        KeyGenParameterSpec.Builder(keyAlias ?: KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
          .setBlockModes(cryptography.blockMode)
          .setEncryptionPaddings(cryptography.padding)
          .setUserAuthenticationRequired(authenticationRequired)
          .setInvalidatedByBiometricEnrollment(true)
          .setRandomizedEncryptionRequired(true)
          .build()
      )
    }.generateKey()
  }
}
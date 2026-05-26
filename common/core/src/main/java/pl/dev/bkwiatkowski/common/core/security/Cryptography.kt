package pl.dev.bkwiatkowski.common.core.security

import android.security.keystore.KeyProperties

sealed class Cryptography(
  val algorithm: String,
  val blockMode: String,
  val padding: String,
  val keySize: Int,
) {
  data object AES_CBC_PKCS7: Cryptography(
    algorithm = KeyProperties.KEY_ALGORITHM_AES,
    blockMode = KeyProperties.BLOCK_MODE_CBC,
    padding = KeyProperties.ENCRYPTION_PADDING_PKCS7,
    keySize = 256,
  )
  data object AES_GCM_NoPadding: Cryptography(
    algorithm = KeyProperties.KEY_ALGORITHM_AES,
    blockMode = KeyProperties.BLOCK_MODE_GCM,
    padding = KeyProperties.ENCRYPTION_PADDING_NONE,
    keySize = 256,
  )
  data object RSA: Cryptography(
    algorithm = KeyProperties.KEY_ALGORITHM_RSA,
    blockMode = KeyProperties.BLOCK_MODE_ECB,
    padding = KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1,
    keySize = 2048,
  )

  fun getTransformation(): String =
    "$algorithm/$blockMode/$padding"

  companion object {
    const val SALT_SIZE = 16
    const val DERIVED_KEY_LENGTH = 256
    const val DERIVED_KEY_ITERATIONS = 600000
    const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256"
  }
}
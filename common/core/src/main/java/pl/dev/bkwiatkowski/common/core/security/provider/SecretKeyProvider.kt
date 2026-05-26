package pl.dev.bkwiatkowski.common.core.security.provider

import pl.dev.bkwiatkowski.common.core.security.Cryptography
import javax.crypto.SecretKey

interface SecretKeyProvider {
  fun getKeyStoreSecretKey(
    cryptography: Cryptography,
    authenticationRequired: Boolean = false,
    keyAlias: String? = null,
  ): SecretKey
  fun getSecretKeyFromBase(cryptography: Cryptography, base: CharArray, salt: ByteArray): SecretKey
}
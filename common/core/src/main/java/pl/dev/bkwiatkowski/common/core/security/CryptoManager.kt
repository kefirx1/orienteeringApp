package pl.dev.bkwiatkowski.common.core.security

import javax.crypto.Cipher
import javax.crypto.SecretKey

interface CryptoManager {
  fun getBaseEncryptCipher(
    cryptography: Cryptography,
    key: SecretKey,
  ): Cipher

  fun getBaseDecryptCipher(
    cryptography: Cryptography,
    key: SecretKey,
    encryptedData: ByteArray,
  ): Cipher

  fun encryptData(
    data: ByteArray,
    cryptography: Cryptography,
  ): ByteArray?

  fun decryptData(
    data: ByteArray,
    cryptography: Cryptography,
  ): ByteArray?

  fun encryptWithKey(
    data: ByteArray,
    key: SecretKey,
    initialCipher: Cipher? = null,
    cryptography: Cryptography = Cryptography.AES_GCM_NoPadding,
  ): ByteArray?

  fun decryptWithKey(
    data: ByteArray,
    key: SecretKey,
    initialCipher: Cipher? = null,
    cryptography: Cryptography = Cryptography.AES_GCM_NoPadding,
  ): ByteArray?

}
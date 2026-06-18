package pl.dev.bkwiatkowski.common.security

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.CryptoManager
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec

class CryptoManagerImpl : CryptoManager {

  override fun getBaseEncryptCipher(
    cryptography: Cryptography,
    key: SecretKey,
  ): Either<DomainError, Cipher> = either {
    getCipher(cryptography = cryptography).apply {
      init(Cipher.ENCRYPT_MODE, key)
    }
  }

  override fun getBaseDecryptCipher(
    cryptography: Cryptography,
    key: SecretKey,
    encryptedData: ByteArray,
  ): Either<DomainError, Cipher> = either {
    getCipher(cryptography = cryptography).apply {
      val ivSize = encryptedData[0].toInt()
      val iv = encryptedData.copyOfRange(1, ivSize + 1)

      init(
        Cipher.DECRYPT_MODE,
        key,
        if (cryptography == Cryptography.AES_GCM_NoPadding) GCMParameterSpec(128, iv) else IvParameterSpec(iv),
      )
    }
  }

  override fun encryptData(
    data: ByteArray,
    cryptography: Cryptography,
    key: SecretKey,
  ): Either<DomainError, ByteArray> = either {
    val encryptCipher = getCipher(cryptography = cryptography).apply {
      init(Cipher.ENCRYPT_MODE, key)
    }
    val encryptedBytes = encryptCipher.doFinal(data)

    byteArrayOf(encryptCipher.iv.size.toByte()) + encryptCipher.iv + encryptedBytes
  }

  override fun decryptData(
    data: ByteArray,
    cryptography: Cryptography,
    key: SecretKey,
  ): Either<DomainError, ByteArray> = either {
    val cipher = getCipher(cryptography = cryptography)

    val ivSize = data[0].toInt()
    val iv = data.copyOfRange(1, ivSize + 1)
    val encryptedData = data.copyOfRange(ivSize + 1, data.size)

    val spec = if (cryptography == Cryptography.AES_GCM_NoPadding) {
      GCMParameterSpec(128, iv)
    } else {
      IvParameterSpec(iv)
    }

    cipher.init(Cipher.DECRYPT_MODE, key, spec)
    cipher.doFinal(encryptedData)
  }


  override fun encryptWithKey(
    data: ByteArray,
    key: SecretKey,
    initialCipher: Cipher?,
    cryptography: Cryptography,
  ): Either<DomainError, ByteArray> = either {
    val cipher = initialCipher ?: getCipher(cryptography = cryptography).apply {
      init(Cipher.ENCRYPT_MODE, key)
    }

    val encryptedData = cipher.doFinal(data)
    byteArrayOf(cipher.iv.size.toByte()) + cipher.iv + encryptedData
  }

  override fun decryptWithKey(
    data: ByteArray,
    key: SecretKey,
    initialCipher: Cipher?,
    cryptography: Cryptography,
  ): Either<DomainError, ByteArray> = either {
    val cipher = initialCipher ?: getCipher(cryptography = cryptography)

    val ivSize = data[0].toInt()
    val iv = data.copyOfRange(1, ivSize + 1)
    val data = data.copyOfRange(ivSize + 1, data.size)

    if (initialCipher == null) {
      cipher.init(Cipher.DECRYPT_MODE, key, if (cryptography == Cryptography.AES_GCM_NoPadding) GCMParameterSpec(128, iv) else IvParameterSpec(iv))
    }

    cipher.doFinal(data)
  }

  private fun getCipher(cryptography: Cryptography): Cipher =
    Cipher.getInstance(cryptography.getTransformation())

}
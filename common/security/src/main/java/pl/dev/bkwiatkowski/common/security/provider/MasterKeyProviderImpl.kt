package pl.dev.bkwiatkowski.common.security.provider

import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.security.MasterKeyDataStore
import java.security.SecureRandom
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class MasterKeyProviderImpl(
  private val masterKeyDataStore: MasterKeyDataStore,
): MasterKeyProvider {
  override fun generateMasterKey(): SecretKey {
    val key = ByteArray(Cryptography.AES_GCM_NoPadding.keySize / 8)
    SecureRandom().nextBytes(key)
    val masterKey = SecretKeySpec(key, Cryptography.AES_GCM_NoPadding.algorithm)

    saveDecryptedMasterKey(masterKey = masterKey)

    return masterKey
  }

  override fun saveDecryptedMasterKey(masterKey: SecretKey) {
    masterKeyDataStore.saveKey(secretKey = masterKey)
  }

  override fun getMasterKey(): SecretKey? {
    return masterKeyDataStore.getKey()
  }

  override fun clearCachedKey() {
    masterKeyDataStore.clearKey()
  }
}
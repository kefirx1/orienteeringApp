package pl.dev.bkwiatkowski.common.core.security.provider

import javax.crypto.SecretKey

interface MasterKeyProvider {
  fun generateMasterKey(): SecretKey
  fun getMasterKey(): SecretKey?

  fun saveDecryptedMasterKey(masterKey: SecretKey)

  fun clearCachedKey()
}
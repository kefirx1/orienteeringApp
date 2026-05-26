package pl.dev.bkwiatkowski.common.security

import javax.crypto.SecretKey

interface MasterKeyDataStore {
  fun saveKey(secretKey: SecretKey)
  fun getKey(): SecretKey?

  fun clearKey()
}
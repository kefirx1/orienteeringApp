package pl.dev.bkwiatkowski.technical.user.data.datastore

import pl.dev.bkwiatkowski.common.security.MasterKeyDataStore
import javax.crypto.SecretKey

class MasterKeyCacheDataStore : MasterKeyDataStore {
  var masterKey: SecretKey? = null

  override fun saveKey(secretKey: SecretKey) {
    masterKey = secretKey
  }

  override  fun getKey(): SecretKey? = masterKey

  override fun clearKey() {
    masterKey = null
  }
}
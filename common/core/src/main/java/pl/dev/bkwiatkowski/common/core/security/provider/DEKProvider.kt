package pl.dev.bkwiatkowski.common.core.security.provider

import javax.crypto.SecretKey

interface DEKProvider {
  fun getDEK(): SecretKey
}
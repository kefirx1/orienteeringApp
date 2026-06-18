package pl.dev.bkwiatkowski.common.core.security.generator

interface SecureRandomGenerator {
  fun getSecret(size: Int): ByteArray
}
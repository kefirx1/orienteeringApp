package pl.dev.bkwiatkowski.common.security.provider

import pl.dev.bkwiatkowski.common.core.security.generator.SecureRandomGenerator
import java.security.SecureRandom

class SecureRandomGeneratorImpl: SecureRandomGenerator {
  override fun getSecret(size: Int): ByteArray {
    val secret = ByteArray(size)
    SecureRandom().nextBytes(secret)

    return secret
  }
}
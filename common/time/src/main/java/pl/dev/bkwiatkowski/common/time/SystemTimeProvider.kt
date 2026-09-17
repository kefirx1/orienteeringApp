package pl.dev.bkwiatkowski.common.time

import pl.dev.bkwiatkowski.common.core.time.TimeProvider

class SystemTimeProvider : TimeProvider {
  override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
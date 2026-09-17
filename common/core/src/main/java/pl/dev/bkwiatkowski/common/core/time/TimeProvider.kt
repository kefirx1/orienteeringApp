package pl.dev.bkwiatkowski.common.core.time

interface TimeProvider {
  fun currentTimeMillis(): Long
}
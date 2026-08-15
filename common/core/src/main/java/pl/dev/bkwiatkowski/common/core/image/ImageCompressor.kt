package pl.dev.bkwiatkowski.common.core.image

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface ImageCompressor {
  suspend fun compress(bytes: ByteArray, qualityPercent: Int): Either<DomainError, ByteArray>
}

package pl.dev.bkwiatkowski.common.core.storage

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface Base64Coder {
  fun decode(data: String?): Either<DomainError, ByteArray>
  fun encode(data: ByteArray?): Either<DomainError, String>
}
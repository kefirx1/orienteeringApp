package pl.dev.bkwiatkowski.common.core.storage

import java.lang.reflect.Type
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface JsonSerializer {
  fun <T> serialize(data: T): Either<DomainError, String>
  fun <T> deserialize(serializedData: String?, type: Type): Either<DomainError, T>
}
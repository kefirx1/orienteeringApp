package pl.dev.bkwiatkowski.common.security.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.security.Cryptography
import pl.dev.bkwiatkowski.common.core.security.usecase.GenerateSaltUC
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import java.security.SecureRandom

class GenerateSaltUCImpl: GenerateSaltUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, ByteArray> =
    either {
      val salt = ByteArray(Cryptography.SALT_SIZE)
      SecureRandom().nextBytes(salt)

      salt
    }
}
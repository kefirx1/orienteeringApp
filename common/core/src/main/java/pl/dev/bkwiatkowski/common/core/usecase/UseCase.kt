package pl.dev.bkwiatkowski.common.core.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError

interface UseCase<PARAMS: UseCase.Params, RESULT> {
  suspend operator fun invoke(params: PARAMS): Either<DomainError, RESULT>

  interface Params
}
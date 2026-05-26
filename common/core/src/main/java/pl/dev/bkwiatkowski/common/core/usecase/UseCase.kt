package pl.dev.bkwiatkowski.common.core.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError

interface EitherUseCase<PARAMS: UseCase.Params, RESULT>: UseCase<PARAMS, Either<DomainError, RESULT>>

interface UseCase<PARAMS: UseCase.Params, RESULT> {
  suspend operator fun invoke(params: PARAMS): RESULT

  interface Params {
    object Empty : Params
  }
}
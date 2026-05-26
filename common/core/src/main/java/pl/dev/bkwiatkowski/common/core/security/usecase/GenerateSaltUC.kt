package pl.dev.bkwiatkowski.common.core.security.usecase

import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase

interface GenerateSaltUC: EitherUseCase<UseCase.Params.Empty, ByteArray>

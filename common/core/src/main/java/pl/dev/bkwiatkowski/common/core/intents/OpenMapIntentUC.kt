package pl.dev.bkwiatkowski.common.core.intents

import pl.dev.bkwiatkowski.common.core.usecase.UseCase

interface OpenMapIntentUC : UseCase<OpenMapIntentUC.Params, Unit> {
  data class Params(
    val latitude: Float,
    val longitude: Float,
    val pinLabel: String?,
  ) : UseCase.Params
}

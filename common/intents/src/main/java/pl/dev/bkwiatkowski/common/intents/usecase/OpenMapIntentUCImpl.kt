package pl.dev.bkwiatkowski.common.intents.usecase

import pl.dev.bkwiatkowski.common.core.intents.OpenMapIntentUC
import pl.dev.bkwiatkowski.common.intents.IntentsManager

class OpenMapIntentUCImpl(
  private val intentsManager: IntentsManager,
) : OpenMapIntentUC {
  override suspend fun invoke(params: OpenMapIntentUC.Params) {
    intentsManager.startMapIntent(
      latitude = params.latitude,
      longitude = params.longitude,
      pinLabel = params.pinLabel,
    )
  }
}

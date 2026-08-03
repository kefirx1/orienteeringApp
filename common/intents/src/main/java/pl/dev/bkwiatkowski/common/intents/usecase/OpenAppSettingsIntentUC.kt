package pl.dev.bkwiatkowski.common.intents.usecase

import pl.dev.bkwiatkowski.common.core.intents.OpenAppSettingsIntentUC
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.intents.IntentsManager

class OpenAppSettingsIntentUCImpl(
  private val intentsManager: IntentsManager,
) : OpenAppSettingsIntentUC {
  override suspend fun invoke(params: UseCase.Params.Empty) {
    intentsManager.startAppSettingsIntent()
  }
}
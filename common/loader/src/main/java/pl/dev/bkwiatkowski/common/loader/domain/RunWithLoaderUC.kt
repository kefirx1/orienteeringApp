package pl.dev.bkwiatkowski.common.loader.domain

import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.loader.LoaderManager

class RunWithLoaderUCImpl(
  private val loaderManager: LoaderManager,
) : RunWithLoaderUC {
  override suspend operator fun <RESULT> invoke(action: suspend () -> RESULT): RESULT {
    loaderManager.showLoader()
    val result = action()
    loaderManager.hideLoader()
    return result
  }

}
package pl.dev.bkwiatkowski.common.loader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface LoaderManager {
  fun visibilityMonitor(): Flow<Boolean>
  suspend fun showLoader()
  suspend fun hideLoader()
}

class LoaderManagerImpl: LoaderManager {
  private val loaderStatus: MutableSharedFlow<Boolean> = MutableSharedFlow()

  override fun visibilityMonitor(): Flow<Boolean> = loaderStatus

  override suspend fun showLoader() {
    loaderStatus.emit(true)
  }

  override suspend fun hideLoader() {
    loaderStatus.emit(false)
  }
}

package pl.dev.bkwiatkowski.common.activityconnector

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class ActivityForResult<INTENT, RESULT>: ActivityConnector {

  abstract val resultContract: ActivityResultContract<INTENT, RESULT>

  protected var activity: AppCompatActivity? = null

  private var activityResultLauncher: ActivityResultLauncher<INTENT>? = null
  private var result: MutableSharedFlow<RESULT> = MutableSharedFlow()

  override fun connect(activity: AppCompatActivity) {
    clearResult()

    this.activity = activity

    activityResultLauncher = activity.registerForActivityResult(
      resultContract
    ) { result ->
      activity.lifecycleScope.launch {
        this@ActivityForResult.result.emit(result)
      }
    }
  }

  protected suspend fun launchActivityForResult(intent: INTENT): RESULT {
    activityResultLauncher?.launch(intent)
    return result.first()
  }

  private fun clearResult() {
    activityResultLauncher = null
    activity = null
  }
}
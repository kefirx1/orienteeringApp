package pl.dev.bkwiatkowski.common.lifecycle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag

interface LifecycleMonitorActivityConnector : ActivityConnector

interface LifecycleMonitor {
  fun activityMonitor(): Flow<Lifecycle.Event>

  fun screenMonitor(): Flow<Lifecycle.Event>

  fun setScreenLifecycleOwner(lifecycleOwner: LifecycleOwner)
}

class LifecycleMonitorImpl: LifecycleEventObserver, LifecycleMonitor, LifecycleMonitorActivityConnector {
  lateinit var activity: AppCompatActivity

  private var screenLifecycleOwner: LifecycleOwner? = null
  private var screenObserver: LifecycleEventObserver? = null

  private val activityLifecycleState: MutableSharedFlow<Lifecycle.Event> = MutableSharedFlow(replay = 1)
  private val screenLifecycleState: MutableSharedFlow<Lifecycle.Event> = MutableSharedFlow(replay = 1)

  override fun connect(activity: AppCompatActivity) {
    this.activity = activity
    this.activity.lifecycle.addObserver(this)
  }

  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    Log.i(
      tag = Tag(this),
      message = "Activity Lifecycle event: $event",
    )

    activityLifecycleState.tryEmit(value = event)
  }

  override fun setScreenLifecycleOwner(lifecycleOwner: LifecycleOwner) {
    if (screenLifecycleOwner == lifecycleOwner && screenObserver != null) return

    detachPreviousScreenObserver()
    attachScreenObserver(lifecycleOwner)
  }

  private fun detachPreviousScreenObserver() {
    screenObserver?.let { observer ->
      runCatching { screenLifecycleOwner?.lifecycle?.removeObserver(observer) }
    }
    screenObserver = null
    screenLifecycleOwner = null
  }

  private fun attachScreenObserver(lifecycleOwner: LifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      Log.i(tag = Tag(this), message = "Screen Lifecycle event: $event")
      screenLifecycleState.tryEmit(value = event)

      if (event == Lifecycle.Event.ON_DESTROY) {
        detachPreviousScreenObserver()
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)
    screenObserver = observer
    screenLifecycleOwner = lifecycleOwner
  }

  override fun activityMonitor(): Flow<Lifecycle.Event> = activityLifecycleState

  override fun screenMonitor(): Flow<Lifecycle.Event> = screenLifecycleState
}

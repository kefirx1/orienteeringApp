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
  fun monitor(): Flow<Lifecycle.Event>
}

class LifecycleMonitorImpl: LifecycleEventObserver, LifecycleMonitor, LifecycleMonitorActivityConnector {
  lateinit var activity: AppCompatActivity

  private val currentLifecycleState: MutableSharedFlow<Lifecycle.Event> = MutableSharedFlow(replay = 1)

  override fun connect(activity: AppCompatActivity) {
    this.activity = activity
    this.activity.lifecycle.addObserver(this)
  }

  override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    Log.i(
      tag = Tag(this),
      message = "Lifecycle event: $event",
    )

    currentLifecycleState.tryEmit(value = event)
  }

  override fun monitor(): Flow<Lifecycle.Event> = currentLifecycleState
}

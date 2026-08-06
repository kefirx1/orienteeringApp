package pl.dev.bkwiatkowski.orienteeringapp.core.lifecycle

import androidx.appcompat.app.AppCompatActivity
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.lifecycle.LifecycleMonitorActivityConnector
import pl.dev.bkwiatkowski.common.camera.CameraActivityConnector
import pl.dev.bkwiatkowski.common.intents.IntentsActivityConnector
import pl.dev.bkwiatkowski.common.permission.PermissionsActivityConnector

class ActivityConnectorImpl(
  private val permissionsActivityConnector: PermissionsActivityConnector,
  private val cameraActivityConnector: CameraActivityConnector,
  private val intentsActivityConnector: IntentsActivityConnector,
  private val lifecycleMonitorActivityConnector: LifecycleMonitorActivityConnector,
) : ActivityConnector {
  override fun connect(activity: AppCompatActivity) {
    listOf<ActivityConnector>(
      permissionsActivityConnector,
      cameraActivityConnector,
      intentsActivityConnector,
      lifecycleMonitorActivityConnector,
    ).forEach { activityResultLauncher ->
      activityResultLauncher.connect(activity = activity)
    }
  }
}
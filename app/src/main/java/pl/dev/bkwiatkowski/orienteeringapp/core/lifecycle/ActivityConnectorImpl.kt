package pl.dev.bkwiatkowski.orienteeringapp.core.lifecycle

import androidx.appcompat.app.AppCompatActivity
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.permission.PermissionsActivityConnector

class ActivityConnectorImpl(
  private val permissionsActivityConnector: PermissionsActivityConnector,
) : ActivityConnector {
  override fun connect(activity: AppCompatActivity) {
    listOf<ActivityConnector>(
      permissionsActivityConnector,
    ).forEach { activityResultLauncher ->
      activityResultLauncher.connect(activity = activity)
    }
  }
}
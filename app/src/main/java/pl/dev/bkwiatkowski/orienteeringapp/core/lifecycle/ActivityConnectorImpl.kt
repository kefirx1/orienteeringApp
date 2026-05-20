package pl.dev.bkwiatkowski.orienteeringapp.core.lifecycle

import androidx.appcompat.app.AppCompatActivity
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector

class ActivityConnectorImpl : ActivityConnector {
  override fun connect(activity: AppCompatActivity) {
    listOf<ActivityConnector>(
    ).forEach { activityResultLauncher ->
      activityResultLauncher.connect(activity = activity)
    }
  }
}
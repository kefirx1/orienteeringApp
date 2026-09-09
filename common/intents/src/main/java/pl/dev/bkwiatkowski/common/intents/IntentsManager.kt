package pl.dev.bkwiatkowski.common.intents

import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.usecase.either

interface IntentsActivityConnector : ActivityConnector

interface IntentsManager {
  suspend fun startAppSettingsIntent()
  suspend fun startMapIntent(
    latitude: Float,
    longitude: Float,
    pinLabel: String?,
  )
}

class IntentsManagerImpl : IntentsManager, IntentsActivityConnector {
  lateinit var activity: AppCompatActivity

  override suspend fun startAppSettingsIntent() {
    activity.startActivity(
      Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = ("package:" + activity.packageName).toUri()
      }
    )
  }

  override suspend fun startMapIntent(latitude: Float, longitude: Float, pinLabel: String?) {
    either {
      val mapUri = if (pinLabel.isNullOrEmpty()) {
        "geo:$latitude,$longitude?z=15".toUri()
      } else {
        "geo:$latitude,$longitude?z=15&q=$latitude,$longitude($pinLabel)".toUri()
      }
      activity.startActivity(Intent(Intent.ACTION_VIEW, mapUri))
    }.onLeft {
      Log.e(
        tag = Tag(this@IntentsManagerImpl),
        message = "Failed to start map intent",
      )
    }
  }

  override fun connect(activity: AppCompatActivity) {
    this.activity = activity
  }
}
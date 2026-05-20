package pl.dev.bkwiatkowski.common.activityconnector

import androidx.appcompat.app.AppCompatActivity

interface ActivityConnector {
  fun connect(activity: AppCompatActivity)
}
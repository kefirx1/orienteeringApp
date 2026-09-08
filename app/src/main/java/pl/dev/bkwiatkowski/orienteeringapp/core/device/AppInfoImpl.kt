package pl.dev.bkwiatkowski.orienteeringapp.core.device

import pl.dev.bkwiatkowski.common.core.device.AppInfo
import pl.dev.bkwiatkowski.orienteeringapp.BuildConfig

class AppInfoImpl : AppInfo {
  override val appVersion: String
    get() = BuildConfig.VERSION_NAME

  override val packageName: String
    get() = BuildConfig.APPLICATION_ID
}
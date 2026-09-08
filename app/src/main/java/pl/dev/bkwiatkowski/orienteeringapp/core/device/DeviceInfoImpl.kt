package pl.dev.bkwiatkowski.orienteeringapp.core.device

import android.os.Build
import pl.dev.bkwiatkowski.common.core.device.DeviceInfo

class DeviceInfoImpl : DeviceInfo {

  override val deviceModel: String
    get() = Build.MODEL

  override val androidSdkInt: Int
    get() = Build.VERSION.SDK_INT
}

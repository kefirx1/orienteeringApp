package pl.dev.bkwiatkowski.common.permission

import android.Manifest
import android.os.Build
import pl.dev.bkwiatkowski.common.core.usecase.Mapper

interface AppPermissionMapper : Mapper<AppPermission, Array<String>>

class AppPermissionMapperImpl : AppPermissionMapper {
  override fun invoke(params: AppPermission): Array<String> = when (params) {
    AppPermission.CAMERA -> arrayOf(Manifest.permission.CAMERA)
    AppPermission.GALLERY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
      )
    } else {
      arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    AppPermission.STORAGE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      arrayOf(
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
      )
    } else {
      arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    AppPermission.WRITE_STORAGE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      emptyArray()
    } else {
      arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    AppPermission.LOCATION -> arrayOf(
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    AppPermission.COARSE_LOCATION -> arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
    AppPermission.BACKGROUND_LOCATION -> arrayOf(
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )

    AppPermission.REMOTE_NOTIFICATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
      emptyArray()
    }

    AppPermission.RECORD_AUDIO -> arrayOf(Manifest.permission.RECORD_AUDIO)
    AppPermission.BLUETOOTH_LE ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
          Manifest.permission.BLUETOOTH_CONNECT,
          Manifest.permission.BLUETOOTH_SCAN,
          Manifest.permission.BLUETOOTH_ADVERTISE,
        )
      } else {
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
      }

    AppPermission.BLUETOOTH_SCAN ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN)
      } else {
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
      }

    AppPermission.BLUETOOTH_ADVERTISE ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE)
      } else {
        emptyArray()
      }

    AppPermission.BLUETOOTH_CONNECT ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_CONNECT)
      } else {
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
      }

    AppPermission.CONTACTS -> arrayOf(
      Manifest.permission.READ_CONTACTS,
      Manifest.permission.WRITE_CONTACTS
    )

    AppPermission.MOTION ->
      arrayOf(
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.BODY_SENSORS
      )
  }
}
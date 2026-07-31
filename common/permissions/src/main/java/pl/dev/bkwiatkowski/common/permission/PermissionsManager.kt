package pl.dev.bkwiatkowski.common.permission

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import pl.dev.bkwiatkowski.common.activityconnector.ActivityConnector
import pl.dev.bkwiatkowski.common.activityconnector.ActivityForResult

interface PermissionsActivityConnector : ActivityConnector

interface PermissionsManager {
  suspend fun requestPermission(permission: AppPermission): PermissionResult
  suspend fun isPermissionGranted(permission: AppPermission): Boolean
}

class PermissionManagerImpl(
  private val appPermissionMapper: AppPermissionMapper,
): ActivityForResult<Array<String>, Map<String, Boolean>>(), PermissionsManager, PermissionsActivityConnector  {

  override val resultContract: ActivityResultContract<Array<String>, Map<String, Boolean>> =
    ActivityResultContracts.RequestMultiplePermissions()

  override suspend fun requestPermission(permission: AppPermission): PermissionResult {
    launchActivityForResult(
      intent = appPermissionMapper(permission)
    ).let { result ->
      return when (result.values.all { it }) {
        true -> PermissionResult.Granted
        false -> {
          result.entries.forEach { (permissionString, granted) ->
            if (!granted) {
              if (activity?.shouldShowRequestPermissionRationale(permissionString) == false) {
                return PermissionResult.DeniedForever
              }
            }
          }

          return PermissionResult.Denied
        }
      }
    }
  }

  override suspend fun isPermissionGranted(permission: AppPermission): Boolean =
    appPermissionMapper(permission).all { mappedPermission ->
      when (activity?.checkSelfPermission(mappedPermission)) {
        PackageManager.PERMISSION_GRANTED -> PermissionResult.Granted
        PackageManager.PERMISSION_DENIED -> PermissionResult.Denied
        else -> PermissionResult.DeniedForever
      } == PermissionResult.Granted
    }

}
package pl.dev.bkwiatkowski.common.permission

sealed interface PermissionResult {
  data object Granted : PermissionResult
  data object Denied : PermissionResult
  data object DeniedForever : PermissionResult
}

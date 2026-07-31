package pl.dev.bkwiatkowski.common.localization

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.localization.GpsManager
import pl.dev.bkwiatkowski.common.core.usecase.Either
import kotlin.coroutines.resume

class GpsManagerImpl(
  private val context: Context,
  private val locationProvider: FusedLocationProviderClient,
) : GpsManager {

  val request by lazy {
    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
      .setMinUpdateIntervalMillis(2000L)
      .build()
  }

  @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
  override suspend fun getLocationFlow(): Flow<Location> = callbackFlow {
    val callback = object : LocationCallback() {
      override fun onLocationResult(result: LocationResult) {
        result.lastLocation?.let { location ->
          trySend(element = location)
        }
      }
    }
    locationProvider.requestLocationUpdates(request, callback, Looper.getMainLooper())

    awaitClose {
      locationProvider.removeLocationUpdates(callback)
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
  override suspend fun getCurrentLocation(): Either<DomainError, Location> = suspendCancellableCoroutine { continuation ->
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGPSEnabled || !isNetworkEnabled) {
      continuation.resume(
        value = Either.Left(
          value = DomainError.Custom(IllegalStateException("GPS is not enabled")),
        ),
      )
    }

    locationProvider.lastLocation
      .addOnSuccessListener { location ->
        continuation.resume(value = Either.Right(value = location))
      }
      .addOnFailureListener { error ->
        continuation.resume(value = Either.Left(value = DomainError.Custom(error)))
      }
  }
}
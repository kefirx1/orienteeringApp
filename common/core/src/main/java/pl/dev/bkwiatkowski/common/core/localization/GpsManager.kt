package pl.dev.bkwiatkowski.common.core.localization

import android.location.Location
import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either

interface GpsManager {
  suspend fun getLocationFlow(): Flow<Location>
  suspend fun getCurrentLocation(): Either<DomainError, Location>
}
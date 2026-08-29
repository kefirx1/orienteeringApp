package pl.dev.bkwiatkowski.feature.event.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.file.FileExtension
import pl.dev.bkwiatkowski.common.core.storage.file.LocalFileManager
import pl.dev.bkwiatkowski.common.core.storage.provider.DataStoreProvider
import pl.dev.bkwiatkowski.common.core.storage.provider.DatabaseProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.storage.converter.LocalDateTimeConverter
import pl.dev.bkwiatkowski.feature.event.data.database.EventDatabase
import pl.dev.bkwiatkowski.feature.event.data.database.WaypointVisitEntity
import pl.dev.bkwiatkowski.feature.event.data.mapper.toDomain
import pl.dev.bkwiatkowski.feature.event.data.mapper.toDto
import pl.dev.bkwiatkowski.feature.event.data.model.MobileEventDetailsDto
import pl.dev.bkwiatkowski.feature.event.domain.model.EventWaypointVisitRecord
import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import java.io.File
import java.time.LocalDateTime

class EventRepositoryImpl(
  private val databaseProvider: DatabaseProvider,
  private val masterKeyProvider: MasterKeyProvider,
  private val localFileManager: LocalFileManager,
  private val localDateTimeConverter: LocalDateTimeConverter,
  private val dataStoreProvider: DataStoreProvider,
) : EventRepository {
  private val localVisitsFlow: MutableSharedFlow<WaypointVisitResponse> = MutableSharedFlow()

  companion object {
    private const val EVENT_DETAILS_STORE_PREFIX = "EVENT_DETAILS_"
    private const val LAST_SAVED_EVENT_ID_KEY = "LAST_SAVED_EVENT_ID"
  }

  private fun getDatabase(): Either<DomainError, EventDatabase> = either {
    val masterKey = masterKeyProvider.getMasterKey().getRight()

    databaseProvider.getDatabase(
      databaseName = EventDatabase.EVENT_DATABASE_NAME,
      databaseClass = EventDatabase::class.java,
      masterKey = masterKey,
      typeConverters = listOf(
        localDateTimeConverter,
      )
    ).getRight()
  }

  override fun observeLocalVisits(): Flow<WaypointVisitResponse> = localVisitsFlow

  override suspend fun publishWaypointVisit(
    waypointId: Int,
    visitedAt: LocalDateTime,
  ): Either<DomainError, Unit> = either {
    localVisitsFlow.emit(
      value = WaypointVisitResponse(
        lastVisitedWaypoint = SessionWaypointDetail(
          waypointId = waypointId,
          visitedAt = visitedAt,
        ),
      ),
    )
  }

  override suspend fun getUnsentVisitsForSession(sessionUuid: String): Either<DomainError, List<EventWaypointVisitRecord>> =
    either {
      getDatabase().getRight().waypointVisitDao().findBySessionUuid(sessionUuid = sessionUuid)
        .filter { record -> !record.sendOnBackend }
        .map { data ->
          EventWaypointVisitRecord(
            id = data.id,
            waypointId = data.waypointId,
            visitedAt = data.visitedAt,
            imagePath = data.imagePath,
            sendOnBackend = data.sendOnBackend,
          )
        }
    }

  override suspend fun getAllVisitsForSession(sessionUuid: String): Either<DomainError, List<EventWaypointVisitRecord>> =
    either {
      getDatabase().getRight().waypointVisitDao().findBySessionUuid(sessionUuid = sessionUuid)
        .map { data ->
          EventWaypointVisitRecord(
            id = data.id,
            waypointId = data.waypointId,
            visitedAt = data.visitedAt,
            imagePath = data.imagePath,
            sendOnBackend = data.sendOnBackend,
          )
        }
    }

  override suspend fun readImageBytes(path: String): Either<DomainError, ByteArray> = either {
    val file = File(path)
    val uri = Uri.fromFile(file)
    localFileManager.readBytesFromUri(uri).getRight()
  }

  override suspend fun saveWaypointVisit(
    waypointId: Int,
    visitedAt: LocalDateTime,
    imageBytes: ByteArray,
    sessionUuid: String,
  ): Either<DomainError, Unit> = either {
    val file = localFileManager.saveFile(
      fileName = getFileNameForWaypointVisit(
        waypointId = waypointId,
        visitedAt = visitedAt,
      ),
      extension = FileExtension.JPG,
      bytes = imageBytes,
    ).getRight()

    getDatabase().getRight().waypointVisitDao().insert(
      visit = WaypointVisitEntity(
        waypointId = waypointId,
        visitedAt = visitedAt,
        imagePath = file.absolutePath,
        sessionUuid = sessionUuid,
        sendOnBackend = false,
      )
    )
  }

  override suspend fun markVisitAsSent(
    waypointId: Int,
    sessionUuid: String
  ): Either<DomainError, Unit> = either {
    getDatabase().getRight().waypointVisitDao().updateStatusByWaypointIdAndSessionUuid(
      waypointId = waypointId,
      sessionUuid = sessionUuid,
      sendOnBackend = true,
    )
  }

  override suspend fun finishSession(
    sessionUuid: String,
    eventId: Int,
  ): Either<DomainError, Unit> = either {
    val database = getDatabase().getRight()

    database.waypointVisitDao().findBySessionUuid(
      sessionUuid = sessionUuid,
    ).forEach { record ->
      localFileManager.deleteFile(path = record.imagePath).onLeft { error ->
        Log.e(
          tag = Tag(this@EventRepositoryImpl),
          message = "Error deleting file: $error",
        )
      }
    }

    database.waypointVisitDao().deleteBySessionUuid(sessionUuid = sessionUuid)
    clearEventDetails(eventId = eventId).getRight()
  }

  override suspend fun saveEventDetails(eventDetails: MobileEventDetails): Either<DomainError, Unit> =
    either {
      dataStoreProvider.updateDataStoreData(
        dataStoreKey = EVENT_DETAILS_STORE_PREFIX + eventDetails.id,
        data = eventDetails.toDto(),
        dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
      ).getRight()

      dataStoreProvider.updateDataStoreData(
        dataStoreKey = LAST_SAVED_EVENT_ID_KEY,
        data = eventDetails.id,
        dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
      ).getRight()
    }

  override suspend fun getEventDetails(eventId: Int): Either<DomainError, MobileEventDetails> =
    dataStoreProvider.getDataStoreData<MobileEventDetailsDto>(
      dataStoreKey = EVENT_DETAILS_STORE_PREFIX + eventId,
      type = MobileEventDetailsDto::class.java,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
    ).mapRight { it.toDomain() }

  override suspend fun clearEventDetails(eventId: Int): Either<DomainError, Unit> =
    either {
      dataStoreProvider.clearDataStoreData(
        dataStoreKey = EVENT_DETAILS_STORE_PREFIX + eventId,
      ).getRight()

      val lastId = dataStoreProvider.getDataStoreData<Int>(
        dataStoreKey = LAST_SAVED_EVENT_ID_KEY,
        type = Int::class.java,
        dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
      ).getRightOrNull()

      if (lastId != null && lastId == eventId) {
        dataStoreProvider.clearDataStoreData(dataStoreKey = LAST_SAVED_EVENT_ID_KEY).getRight()
      }
    }

  override suspend fun getLastSavedEventDetails(): Either<DomainError, MobileEventDetails> = either {
    val lastId = dataStoreProvider.getDataStoreData<Int>(
      dataStoreKey = LAST_SAVED_EVENT_ID_KEY,
      type = Int::class.java,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
    ).getRightOrNull() ?: raise(error = DomainError.Custom(NullPointerException("There is no saved event details")))

    dataStoreProvider.getDataStoreData<MobileEventDetailsDto>(
      dataStoreKey = EVENT_DETAILS_STORE_PREFIX + lastId,
      type = MobileEventDetailsDto::class.java,
      dataStoreKeyProvider = DataStoreProvider.DataStoreKeyProvider.MasterKey,
    ).mapRight { it.toDomain() }.getRight()
  }

  private fun getFileNameForWaypointVisit(waypointId: Int, visitedAt: LocalDateTime): String =
    "$waypointId-${visitedAt.toString().replace(":", "-")}"
}

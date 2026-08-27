package pl.dev.bkwiatkowski.feature.event.data.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.logger.Log
import pl.dev.bkwiatkowski.common.core.logger.Tag
import pl.dev.bkwiatkowski.common.core.security.provider.MasterKeyProvider
import pl.dev.bkwiatkowski.common.core.storage.file.FileExtension
import pl.dev.bkwiatkowski.common.core.storage.file.LocalFileManager
import pl.dev.bkwiatkowski.common.core.storage.provider.DatabaseProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.storage.converter.LocalDateTimeConverter
import pl.dev.bkwiatkowski.feature.event.data.database.EventDatabase
import pl.dev.bkwiatkowski.feature.event.data.database.WaypointVisitEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pl.dev.bkwiatkowski.feature.event.domain.model.EventWaypointVisitRecord
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import java.time.LocalDateTime
import android.net.Uri
import kotlinx.coroutines.flow.onEach
import java.io.File

class EventRepositoryImpl(
  private val databaseProvider: DatabaseProvider,
  private val masterKeyProvider: MasterKeyProvider,
  private val localFileManager: LocalFileManager,
  private val localDateTimeConverter: LocalDateTimeConverter,
) : EventRepository {
  private val localVisitsFlow: MutableSharedFlow<WaypointVisitResponse> = MutableSharedFlow()

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

  override suspend fun getUnsentVisitsForSession(sessionUuid: String): Either<DomainError, List<EventWaypointVisitRecord>> = either {
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

  override suspend fun getVisitsForWaypoint(waypointId: Int, sessionUuid: String): Either<DomainError, EventWaypointVisitRecord> = either {
    getDatabase().getRight().waypointVisitDao().findByWaypointIdAndSessionUuid(
      waypointId = waypointId,
      sessionUuid = sessionUuid,
    )?.let { data ->
      EventWaypointVisitRecord(
        id = data.id,
        waypointId = data.waypointId,
        visitedAt = data.visitedAt,
        imagePath = data.imagePath,
        sendOnBackend = data.sendOnBackend,
      )
    } ?: raise(error = DomainError.Custom(NullPointerException("No visit record found for waypointId: $waypointId and sessionUuid: $sessionUuid")))
  }

  override suspend fun markVisitAsOnline(waypointId: Int, sessionUuid: String): Either<DomainError, Unit> = either {
    getDatabase().getRight().waypointVisitDao().updateStatusByWaypointIdAndSessionUuid(
      waypointId = waypointId,
      sessionUuid = sessionUuid,
      sendOnBackend = true,
    )
  }

  override suspend fun finishSession(sessionUuid: String): Either<DomainError, Unit> = either {
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
  }

  private fun getFileNameForWaypointVisit(waypointId: Int, visitedAt: LocalDateTime): String =
    "$waypointId-${visitedAt.toString().replace(":", "-")}"
}

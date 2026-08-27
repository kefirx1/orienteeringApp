package pl.dev.bkwiatkowski.feature.event.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WaypointVisitDao {
  @Insert
  suspend fun insert(visit: WaypointVisitEntity): Long

  @Query(value = "SELECT * FROM waypoint_visits WHERE waypointId = :waypointId AND sessionUuid = :sessionUuid LIMIT 1")
  suspend fun findByWaypointIdAndSessionUuid(waypointId: Int, sessionUuid: String): WaypointVisitEntity?

  @Query(value = "SELECT * FROM waypoint_visits WHERE sessionUuid = :sessionUuid")
  suspend fun findBySessionUuid(sessionUuid: String): List<WaypointVisitEntity>

  @Query(value = "DELETE FROM waypoint_visits WHERE sessionUuid = :sessionUuid")
  suspend fun deleteBySessionUuid(sessionUuid: String): Int

  @Query(value = "UPDATE waypoint_visits SET sendOnBackend = :sendOnBackend WHERE waypointId = :waypointId AND sessionUuid = :sessionUuid")
  suspend fun updateStatusByWaypointIdAndSessionUuid(waypointId: Int, sessionUuid: String, sendOnBackend: Boolean): Int
}
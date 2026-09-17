package pl.dev.bkwiatkowski.feature.event.domain.usecase

import android.location.Location
import android.util.Log
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.dev.bkwiatkowski.common.core.localization.GpsManager
import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.core.time.TimeProvider
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.event.domain.model.MapWaypoint

class ObserveWaypointWithAccuracyTimerUCTest {
  private var gpsManager: GpsManager = mockk()
  private var findWaypointFromUserLocationUC: FindWaypointFromUserLocationUC = mockk()
  private var timeProvider: TimeProvider = mockk()
  private var useCase: ObserveWaypointWithAccuracyTimerUC = ObserveWaypointWithAccuracyTimerUCImpl(
    gpsManager = gpsManager,
    findWaypointFromUserLocationUC = findWaypointFromUserLocationUC,
    timeProvider = timeProvider,
  )

  @Before
  fun setup() {
    mockkStatic(Log::class)
    every { Log.i(any(), any()) } returns 0
    every { Log.e(any(), any()) } returns 0
    every { Log.d(any(), any()) } returns 0
    coEvery { timeProvider.currentTimeMillis() } returns 0L
  }

  private fun createLocation(
    accuracy: Float,
    latitude: Double = 52.0,
    longitude: Double = 19.0,
  ): Location =
    mockk(relaxed = true) {
      every { this@mockk.accuracy } returns accuracy
      every { this@mockk.latitude } returns latitude
      every { this@mockk.longitude } returns longitude
    }

  private fun createWaypoint(
    id: Int = 1,
    label: String = "Waypoint $id",
    position: Position = Position(latitude = 52.0, longitude = 19.0),
  ): MapWaypoint = MapWaypoint(id = id, label = label, position = position)

  @Test
  fun `strong accuracy with found waypoint emits StrongAccuracyWithWaypoint`() = runTest {
    val waypoint = createWaypoint()
    val location = createLocation(accuracy = 30f)
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = 30f,
      foundWaypoint = waypoint,
    )

    coEvery { gpsManager.getLocationFlow() } returns flow { emit(location) }
    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.isNotEmpty())
    assertEquals(
      ObserveWaypointWithAccuracyTimerUC.Result.StrongAccuracyWithWaypoint(waypoint),
      emissions.first(),
    )
  }

  @Test
  fun `strong accuracy with no found waypoint emits StrongAccuracyNoWaypoint`() = runTest {
    val waypoint = createWaypoint()
    val location = createLocation(accuracy = 40f)
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = 40f,
      foundWaypoint = null,
    )

    coEvery { gpsManager.getLocationFlow() } returns flow { emit(location) }
    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.isNotEmpty())
    assertEquals(
      ObserveWaypointWithAccuracyTimerUC.Result.StrongAccuracyNoWaypoint,
      emissions.first(),
    )
  }

  @Test
  fun `weak accuracy with found waypoint initializes timer`() = runTest {
    val waypoint = createWaypoint()
    val location = createLocation(accuracy = 100f)
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = 100f,
      foundWaypoint = waypoint,
    )

    coEvery { gpsManager.getLocationFlow() } returns flow { emit(location) }
    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.isNotEmpty())
    val emission = emissions.first()
    assertTrue(emission is ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint)
    assertFalse((emission as ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint).timerExpired)
  }

  @Test
  fun `weak accuracy with no found waypoint initializes timer`() = runTest {
    val waypoint = createWaypoint()
    val location = createLocation(accuracy = 120f)
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = 120f,
      foundWaypoint = null,
    )

    coEvery { gpsManager.getLocationFlow() } returns flow { emit(location) }
    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.isNotEmpty())
    val emission = emissions.first()
    assertTrue(emission is ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyNoWaypoint)
    assertFalse((emission as ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyNoWaypoint).timerExpired)
  }

  @Test
  fun `very weak accuracy emits VeryWeakAccuracy`() = runTest {
    val waypoint = createWaypoint()
    val location = createLocation(accuracy = 160f)
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = 160f,
      foundWaypoint = waypoint,
    )

    coEvery { gpsManager.getLocationFlow() } returns flow { emit(location) }
    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.isNotEmpty())
    assertEquals(
      ObserveWaypointWithAccuracyTimerUC.Result.VeryWeakAccuracy,
      emissions.first(),
    )
  }

  @Test
  fun `weak accuracy timer expires after WEAK_ACCURACY_TIMEOUT_MILLIS`() = runTest {
    val waypoint = createWaypoint()
    val weakLocation = createLocation(accuracy = 100f)
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = 100f,
      foundWaypoint = waypoint,
    )

    val times = listOf(0L, 21_000L)
    var timeCallCount = 0

    coEvery { gpsManager.getLocationFlow() } returns flow {
      emit(weakLocation)
      emit(weakLocation)
    }

    coEvery { timeProvider.currentTimeMillis() } answers {
      val time = times.getOrNull(timeCallCount) ?: 21_000L
      timeCallCount++
      time
    }

    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(value = result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.size >= 2)
    val firstEmission = emissions[0] as ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint
    val secondEmission = emissions[1] as ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint

    assertFalse(firstEmission.timerExpired)
    assertTrue(secondEmission.timerExpired)
  }

  @Test
  fun `improving from weak to strong accuracy cancels timer`() = runTest {
    val waypoint = createWaypoint()
    val weakLocation = createLocation(accuracy = 100f)
    val strongLocation = createLocation(accuracy = 30f)

    val times = listOf(0L, 100L)
    var timeCallCount = 0

    coEvery { gpsManager.getLocationFlow() } returns flow {
      emit(weakLocation)
      emit(strongLocation)
    }

    coEvery { timeProvider.currentTimeMillis() } answers {
      val time = times.getOrNull(timeCallCount) ?: 100L
      timeCallCount++
      time
    }

    val weakResult = FindWaypointFromUserLocationUC.Result(accuracy = 100f, foundWaypoint = waypoint)
    val strongResult = FindWaypointFromUserLocationUC.Result(accuracy = 30f, foundWaypoint = waypoint)

    var callCount = 0
    coEvery { findWaypointFromUserLocationUC(any()) } answers {
      if (callCount == 0) {
        callCount++
        Either.Right(weakResult)
      } else {
        Either.Right(strongResult)
      }
    }

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.size >= 2)
    assertTrue(emissions[0] is ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint)
    assertTrue(emissions[1] is ObserveWaypointWithAccuracyTimerUC.Result.StrongAccuracyWithWaypoint)
  }

  @Test
  fun `very weak accuracy during weak timer cancels timer`() = runTest {
    val waypoint = createWaypoint()
    val weakLocation = createLocation(accuracy = 100f)
    val veryWeakLocation = createLocation(accuracy = 160f)

    val times = listOf(0L, 100L)
    var timeCallCount = 0

    coEvery { gpsManager.getLocationFlow() } returns flow {
      emit(weakLocation)
      emit(veryWeakLocation)
    }

    coEvery { timeProvider.currentTimeMillis() } answers {
      val time = times.getOrNull(timeCallCount) ?: 100L
      timeCallCount++
      time
    }

    val weakResult = FindWaypointFromUserLocationUC.Result(accuracy = 100f, foundWaypoint = waypoint)
    val veryWeakResult = FindWaypointFromUserLocationUC.Result(accuracy = 160f, foundWaypoint = waypoint)

    var callCount = 0
    coEvery { findWaypointFromUserLocationUC(any()) } answers {
      if (callCount == 0) {
        callCount++
        Either.Right(weakResult)
      } else {
        Either.Right(veryWeakResult)
      }
    }

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.size >= 2)
    assertTrue(emissions[0] is ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint)
    assertTrue(emissions[1] is ObserveWaypointWithAccuracyTimerUC.Result.VeryWeakAccuracy,)
  }

  @Test
  fun `accuracy at ACCURACY_THRESHOLD boundary is considered strong`() = runTest {
    val waypoint = createWaypoint()
    val location = createLocation(
      accuracy = ObserveWaypointWithAccuracyTimerUCImpl.ACCURACY_THRESHOLD,
    )
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = ObserveWaypointWithAccuracyTimerUCImpl.ACCURACY_THRESHOLD,
      foundWaypoint = waypoint,
    )

    coEvery { gpsManager.getLocationFlow() } returns flow { emit(location) }
    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.isNotEmpty())
    assertTrue(
      emissions.first() is ObserveWaypointWithAccuracyTimerUC.Result.StrongAccuracyWithWaypoint,
    )
  }

  @Test
  fun `accuracy at VERY_WEAK_THRESHOLD boundary is considered weak`() = runTest {
    val waypoint = createWaypoint()
    val location = createLocation(
      accuracy = ObserveWaypointWithAccuracyTimerUCImpl.VERY_WEAK_THRESHOLD,
    )
    val result = FindWaypointFromUserLocationUC.Result(
      accuracy = ObserveWaypointWithAccuracyTimerUCImpl.VERY_WEAK_THRESHOLD,
      foundWaypoint = waypoint,
    )

    coEvery { gpsManager.getLocationFlow() } returns flow { emit(location) }
    coEvery { findWaypointFromUserLocationUC(any()) } returns Either.Right(result)

    val emissions = useCase(
      waypoints = listOf(waypoint),
      waypointRadiusMeters = 100f,
    ).toList()

    assertTrue(emissions.isNotEmpty())
    assertTrue(
      emissions.first() is ObserveWaypointWithAccuracyTimerUC.Result.WeakAccuracyWithWaypoint,
    )
  }
}

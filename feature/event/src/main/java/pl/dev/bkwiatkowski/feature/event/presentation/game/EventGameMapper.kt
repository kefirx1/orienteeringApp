package pl.dev.bkwiatkowski.feature.event.presentation.game

import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.core.usecase.Mapper

interface EventGameMapper : Mapper<EventGameMapper.Params, EventGameVM.ScreenData> {
  data class Params(
    val state: EventGameVM.State,
    val onBackClick: () -> Unit,
  )
}

class EventGameMapperImpl(
  private val dateFormatter: DateFormatter,
) : EventGameMapper {
  override fun invoke(params: EventGameMapper.Params): EventGameVM.ScreenData =
    when (params.state) {
      is EventGameVM.State.Empty -> EventGameVM.ScreenData.Empty(
        onBackClick = params.onBackClick,
        emptyLabel = "Brak odwiedzonych punktów",
      )
      is EventGameVM.State.Active -> EventGameVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        waypoints = params.state.visitedWaypoints.map { waypoint ->
          EventGameVM.WaypointData(
            visitedTime = dateFormatter.format(
              dateTime = waypoint.visitedAt,
              format = DateFormatter.Format.TIME_ONLY,
            ).let { time ->
              "Odwiedzono o $time"
            },
            label = "Punkt ${waypoint.waypointId}",
          )
        }
      )
    }
}

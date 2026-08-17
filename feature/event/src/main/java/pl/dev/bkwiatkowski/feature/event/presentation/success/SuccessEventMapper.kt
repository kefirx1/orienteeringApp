package pl.dev.bkwiatkowski.feature.event.presentation.success

import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.event.presentation.game.EventGameVM
import javax.inject.Inject

interface SuccessEventMapper : Mapper<SuccessEventMapper.Params, SuccessEventVM.ScreenData> {
  data class Params(
    val state: SuccessEventVM.State,
    val onBackClick: () -> Unit,
  )
}

class SuccessEventMapperImpl @Inject constructor(
  private val dateFormatter: DateFormatter,
) : SuccessEventMapper {
  override fun invoke(params: SuccessEventMapper.Params): SuccessEventVM.ScreenData =
    when (val state = params.state) {
      is SuccessEventVM.State.Active -> {
        val joined = state.finishResponse.participant.joinedAt
        val finished = state.finishResponse.participant.finishedAt

        SuccessEventVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          topAppBarData = TopAppBarData.BackAndTitle(
            onNavigationIconClick = params.onBackClick,
            title = state.eventName,
          ),
          description = "Dziękujemy za rozegranie biegu na orientację, polecamy sprawdzić również pozostałe trasy!",
          startDateTime = dateFormatter.format(dateTime = joined, format = DateFormatter.Format.DATE_TIME),
          finishDateTime = dateFormatter.format(dateTime = finished, format = DateFormatter.Format.DATE_TIME),
          waypoints = state.finishResponse.sessionWaypointDetails.map { waypoint ->
            EventGameVM.WaypointData(
              visitedTime = dateFormatter.format(
                dateTime = waypoint.visitedAt,
                format = DateFormatter.Format.TIME_ONLY,
              ).let { time -> "Odwiedzono o $time" },
              label = "Punkt ${waypoint.waypointId}",
            )
          },
          closButtonData = LargeButtonData.Primary(
            text = "Zamknij",
            onClick = params.onBackClick,
          )
        )
      }
    }
}

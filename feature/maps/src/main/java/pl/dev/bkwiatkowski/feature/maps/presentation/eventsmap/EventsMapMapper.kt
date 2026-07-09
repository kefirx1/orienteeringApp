package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.map.MapComponentData
import pl.dev.bkwiatkowski.common.ui.component.map.MarkerData
import javax.inject.Inject

interface EventsMapMapper : Mapper<EventsMapMapper.Params, EventsMapVM.ScreenData> {
  data class Params(
    val state: EventsMapVM.State,
    val onBackClick: () -> Unit,
  )
}

class EventsMapMapperImpl @Inject constructor() : EventsMapMapper {
  override fun invoke(params: EventsMapMapper.Params): EventsMapVM.ScreenData =
    when (params.state) {
      is EventsMapVM.State.Initialized -> {
        EventsMapVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          mapComponentData = MapComponentData(
            markers = listOf(
              MarkerData(
                position = Position.CENTRAL_POLAND,
                infoCardTitle = "Event 1",
                infoCardBody = "Event 1 description",
                buttonLabel = "Zagraj",
              ),
              MarkerData(
                position = Position(
                  latitude = Position.CENTRAL_POLAND.latitude + 0.5,
                  longitude = Position.CENTRAL_POLAND.longitude + 0.5
                ),
                infoCardTitle = "Event 2",
                infoCardBody = "Event 2 description",
                buttonLabel = "Zagraj",
              ),
              MarkerData(
                position = Position(
                  latitude = Position.CENTRAL_POLAND.latitude - 0.5,
                  longitude = Position.CENTRAL_POLAND.longitude - 0.5
                ),
                infoCardTitle = "Event 3",
                infoCardBody = "Event 3 description",
                buttonLabel = "Zagraj"
              )
            )
          )
        )
      }
    }
}

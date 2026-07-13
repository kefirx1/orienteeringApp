package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.map.MapComponentData
import pl.dev.bkwiatkowski.common.ui.component.map.MarkerData
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventListDetails
import javax.inject.Inject

interface EventsMapMapper : Mapper<EventsMapMapper.Params, EventsMapVM.ScreenData> {
  data class Params(
    val state: EventsMapVM.State,
    val onBackClick: () -> Unit,
    val onEventDetailsClick: (String) -> Unit
  )
}

class EventsMapMapperImpl @Inject constructor() : EventsMapMapper {
  override fun invoke(params: EventsMapMapper.Params): EventsMapVM.ScreenData =
    when (params.state) {
      is EventsMapVM.State.Loading ->
        EventsMapVM.ScreenData.Loading(
          onBackClick = params.onBackClick,
        )
      is EventsMapVM.State.Initialized -> {
        EventsMapVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          mapComponentData = MapComponentData(
            markers = params.state.events?.events?.map { event ->
              event.getMarkerData()
            } ?: emptyList(),
          )
        )
      }
    }

  private fun MobileEventListDetails.getMarkerData() = MarkerData(
    position = Position(
      latitude = this.startLocationY.toDouble(),
      longitude = this.startLocationX.toDouble(),
    ),
    infoCardTitle = this.name,
    infoCardBody = this.description,
    buttonLabel = "Sprawdź",
  )
}

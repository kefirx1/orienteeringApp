package pl.dev.bkwiatkowski.feature.maps.presentation.eventsmap

import pl.dev.bkwiatkowski.common.core.location.Position
import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.map.MapComponentData
import pl.dev.bkwiatkowski.common.ui.component.map.MarkerData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.maps.domain.model.MobileEventListDetails

interface EventsMapMapper : Mapper<EventsMapMapper.Params, EventsMapVM.ScreenData> {
  data class Params(
    val state: EventsMapVM.State,
    val onBackClick: () -> Unit,
    val onEventDetailsClick: (Int) -> Unit
  )
}

class EventsMapMapperImpl : EventsMapMapper {
  override fun invoke(params: EventsMapMapper.Params): EventsMapVM.ScreenData =
    when (params.state) {
      is EventsMapVM.State.Error -> EventsMapVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
      is EventsMapVM.State.Loading ->
        EventsMapVM.ScreenData.Loading(
          onBackClick = params.onBackClick,
        )
      is EventsMapVM.State.Initialized -> {
        EventsMapVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          barData = TopAppBarData.Back(
            onNavigationIconClick = params.onBackClick,
          ),
          mapComponentData = MapComponentData(
            markers = params.state.events?.events?.map { event ->
              event.getMarkerData(onEventClick = params.onEventDetailsClick)
            } ?: emptyList(),
          )
        )
      }
    }

  private fun MobileEventListDetails.getMarkerData(onEventClick: (Int) -> Unit) = MarkerData(
    position = Position(
      latitude = this.startLocationY.toDouble(),
      longitude = this.startLocationX.toDouble(),
    ),
    infoCardTitle = this.name,
    infoCardBody = this.description,
    buttonLabel = "Sprawdź",
    onButtonClick = { onEventClick(this.id) },
  )
}

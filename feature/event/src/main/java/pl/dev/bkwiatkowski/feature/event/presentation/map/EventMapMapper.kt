package pl.dev.bkwiatkowski.feature.event.presentation.map

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImageData
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader

interface EventMapMapper : Mapper<EventMapMapper.Params, EventMapVM.ScreenData> {
  data class Params(
    val state: EventMapVM.State,
    val onBackClick: () -> Unit,
    val onCheckWaypointClick: () -> Unit,
  )
}

class EventMapMapperImpl(
  private val bitmapReader: BitmapReader,
) : EventMapMapper {
  override fun invoke(params: EventMapMapper.Params): EventMapVM.ScreenData =
    when (params.state) {
      is EventMapVM.State.Loading.Error -> EventMapVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
      is EventMapVM.State.Loading.Content -> EventMapVM.ScreenData.Loading(
        onBackClick = params.onBackClick,
      )
      is EventMapVM.State.Active.Error -> EventMapVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
      is EventMapVM.State.Active.Content -> {
        val details = params.state.eventDetails

        EventMapVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          title = details.name,
          mapData = bitmapReader.decode(encoded = details.map.imageData)?.let { bitmap ->
            ZoomImageData(
              bitmap = bitmap,
              contentDescription = "Event map",
            )
          },
          nextWaypointLabel = "Aktualnie poszukiwany punkt: ${params.state.nextWaypoint?.label ?: "Brak"}",
          wrongWaypointInfo = "Odwiedzono niewłaściwe miejsce, musisz szukać innego punktu na mapie!".takeIf { params.state.visitedWrongWaypoint },
          checkWaypointButton = LargeButtonData.Primary(
            text = "Zatwierdź punkt",
            onClick = params.onCheckWaypointClick,
          ).takeIf {
            params.state.currentWaypoint != null && params.state.nextWaypoint != null && params.state.currentWaypoint.id == params.state.nextWaypoint.id
          },
        )
      }
    }
}

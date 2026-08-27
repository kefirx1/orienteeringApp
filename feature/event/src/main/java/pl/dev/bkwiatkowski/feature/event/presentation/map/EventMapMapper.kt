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
    val onCompleteClick: () -> Unit,
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
        val details = params.state.stateData.eventDetails

        EventMapVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          title = details.name,
          mapData = bitmapReader.decode(encoded = details.map.imageData)?.let { bitmap ->
            ZoomImageData(
              bitmap = bitmap,
              contentDescription = "Event map",
            )
          },
          nextWaypointLabel = "Aktualnie poszukiwany punkt: ${params.state.stateData.nextWaypoint?.label ?: "Brak"}",
          wrongWaypointInfo = "Odwiedzono niewłaściwe miejsce, musisz szukać innego punktu na mapie!".takeIf { params.state.stateData.visitedWrongWaypoint },
          checkWaypointButton = LargeButtonData.Primary(
            text = "Zatwierdź punkt",
            onClick = params.onCheckWaypointClick,
          ).takeIf {
            params.state.stateData.currentWaypoint != null && params.state.stateData.nextWaypoint != null && params.state.stateData.currentWaypoint.id == params.state.stateData.nextWaypoint.id
          },
        )
      }
      is EventMapVM.State.Completed.Content -> EventMapVM.ScreenData.Completed(
        onBackClick = params.onBackClick,
        descriptionLabel = "Udało Ci się przejść cały bieg na orientację, możesz teraz potwierdzić przejście trasy przyciskiem poniżej",
        confirmButton = LargeButtonData.Primary(
          text = "Potwierdź ukończenie trasy",
          onClick = params.onCompleteClick,
        ),
      )
      is EventMapVM.State.Completed.Error -> EventMapVM.ScreenData.ErrorScreen(
        onBackClick = params.onBackClick,
        errorData = params.state.errorScreenData,
      )
    }
}

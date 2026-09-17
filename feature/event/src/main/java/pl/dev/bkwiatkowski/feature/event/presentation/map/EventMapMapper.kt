package pl.dev.bkwiatkowski.feature.event.presentation.map

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImageData
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import pl.dev.bkwiatkowski.feature.event.presentation.main.EventMainContract

interface EventMapMapper : Mapper<EventMapMapper.Params, EventMapVM.ScreenData> {
  data class Params(
    val state: EventMapVM.State,
    val onBackClick: () -> Unit,
    val onCheckWaypointClick: () -> Unit,
    val onCompleteClick: () -> Unit,
    val onWeakAccuracyCheckWaypointClick: () -> Unit,
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
          weakAccuracyCheckWaypointButton = LargeButtonData.Secondary(
            text = "Zatwierdź punkt (słaby zasięg)",
            onClick = params.onWeakAccuracyCheckWaypointClick,
          ).takeIf {
            params.state.stateData.nextWaypoint != null &&
                ((params.state.stateData.accuracyState as? EventMainContract.AccuracyState.WeakAccuracy)?.timerExpired == true ||
                    (params.state.stateData.accuracyState is EventMainContract.AccuracyState.VeryWeakAccuracy))
          },
          accuracyInfoMessage = generateAccuracyMessage(params.state.stateData.accuracyState),
          debugCheckWaypointButton = LargeButtonData.Tertiary(
            text = "DEBUG: Zatwierdź punkt",
            onClick = params.onWeakAccuracyCheckWaypointClick,
          ).takeIf {
            params.state.stateData.isDebugLocationEnabled && params.state.stateData.nextWaypoint != null
          },
        )
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
           weakAccuracyCheckWaypointButton = LargeButtonData.Secondary(
             text = "Zatwierdź punkt (słaby zasięg)",
             onClick = params.onWeakAccuracyCheckWaypointClick,
           ).takeIf {
             params.state.stateData.nextWaypoint != null &&
                 ((params.state.stateData.accuracyState as? EventMainContract.AccuracyState.WeakAccuracy)?.timerExpired == true ||
                     (params.state.stateData.accuracyState is EventMainContract.AccuracyState.VeryWeakAccuracy))
           },
           debugCheckWaypointButton = LargeButtonData.Tertiary(
             text = "DEBUG: Zatwierdź punkt",
             onClick = params.onWeakAccuracyCheckWaypointClick,
           ).takeIf {
             params.state.stateData.isDebugLocationEnabled && params.state.stateData.nextWaypoint != null
           },
           accuracyInfoMessage = generateAccuracyMessage(params.state.stateData.accuracyState),
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

  private fun generateAccuracyMessage(accuracyState: EventMainContract.AccuracyState): String? =
    when (accuracyState) {
      is EventMainContract.AccuracyState.WeakAccuracy -> {
        if (accuracyState.timerExpired) {
          "Zasięg nawigacji jest zbyt słaby by wykryć urządzenie, po dotarciu do punktu użyj przycisku do weryfikacji."
        } else {
          "Słaby zasięg GPS. Jeśli jesteś obok punktu kontrolnego, poczekaj w miejscu 20 sekund aby poprawić zasięg."
        }
      }
      EventMainContract.AccuracyState.VeryWeakAccuracy -> {
        "Zasięg nawigacji jest zbyt słaby by wykryć urządzenie, po dotarciu do punktu użyj przycisku do weryfikacji."
      }
      EventMainContract.AccuracyState.StrongAccuracy -> null
    }
}

package pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails

import androidx.compose.material3.SnackbarHostState
import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventStatus

interface EventDetailsMapper : Mapper<EventDetailsMapper.Params, EventDetailsVM.ScreenData> {
  data class Params(
    val state: EventDetailsVM.State,
    val snackbarHostState: SnackbarHostState,
    val onBackClick: () -> Unit,
    val onPlayClick: () -> Unit,
    val onGoToSettingsClick: () -> Unit,
  )
}

class EventDetailsMapperImpl(
  private val dateFormatter: DateFormatter,
  private val bitmapReader: BitmapReader,
) : EventDetailsMapper {
  override fun invoke(params: EventDetailsMapper.Params): EventDetailsVM.ScreenData =
    when (params.state) {
      is EventDetailsVM.State.Loading -> EventDetailsVM.ScreenData.Loading(
        onBackClick = params.onBackClick,
      )
      is EventDetailsVM.State.Initialized.InitializedAlreadyJoined -> EventDetailsVM.ScreenData.MainWithSession(
        onBackClick = params.onBackClick,
        event = params.state.event,
        topAppBarData = TopAppBarData.Back(onNavigationIconClick = params.onBackClick),
        startDateTime = dateFormatter.format(
          dateTime = params.state.event.startDate,
          format = DateFormatter.Format.DATE_TIME,
        ).let { time ->
          "Rozpoczęto: $time"
        },
        map = bitmapReader.decode(encoded = params.state.event.map.imageData),
        playButtonData = when (params.state.event.eventStatus) {
          EventStatus.IN_PROGRESS,
          EventStatus.CONTINUOUS -> LargeButtonData.Primary(
            text = "Kontynuuj",
            onClick = params.onPlayClick,
          )
          EventStatus.PLANNED,
          EventStatus.COMPLETED -> null
        }.takeIf { params.state.event.session?.userCanJoin == true },
        snackbarHostState = params.snackbarHostState,
      )
      is EventDetailsVM.State.Initialized.InitializedNotJoined -> EventDetailsVM.ScreenData.MainWithSession(
        onBackClick = params.onBackClick,
        event = params.state.event,
        topAppBarData = TopAppBarData.Back(onNavigationIconClick = params.onBackClick),
        startDateTime = dateFormatter.format(
          dateTime = params.state.event.startDate,
          format = DateFormatter.Format.DATE_TIME,
        ).let { time ->
          "Rozpoczęto: $time"
        },
        map = bitmapReader.decode(encoded = params.state.event.map.imageData),
        playButtonData = when {
          params.state.deniedForever -> LargeButtonData.Primary(
            text = "Zezwól na lokalizację",
            onClick = params.onGoToSettingsClick,
          )
          params.state.event.eventStatus == EventStatus.CONTINUOUS -> LargeButtonData.Primary(
            text = "Zagraj",
            onClick = params.onPlayClick,
          )
          params.state.event.eventStatus == EventStatus.IN_PROGRESS -> LargeButtonData.Primary(
            text = "Dołącz",
            onClick = params.onPlayClick,
          )
          else -> null
        }.takeIf { params.state.event.session?.userCanJoin == true },
        snackbarHostState = params.snackbarHostState,
      )
      is EventDetailsVM.State.Initialized.InitializedNoSession -> EventDetailsVM.ScreenData.MainNoSession(
        onBackClick = params.onBackClick,
        event = params.state.event,
        topAppBarData = TopAppBarData.Back(onNavigationIconClick = params.onBackClick),
        startDateTime = dateFormatter.format(
          dateTime = params.state.event.startDate,
          format = DateFormatter.Format.DATE_TIME,
        ).let { time ->
          "Rozpoczyna się: $time"
        },
        map = bitmapReader.decode(encoded = params.state.event.map.imageData),
      )
      is EventDetailsVM.State.Initialized.InitializedFinished -> EventDetailsVM.ScreenData.MainFinished(
        onBackClick = params.onBackClick,
        event = params.state.event,
        topAppBarData = TopAppBarData.Back(onNavigationIconClick = params.onBackClick),
        startDateTime = dateFormatter.format(
          dateTime = params.state.event.startDate,
          format = DateFormatter.Format.DATE_TIME,
        ).let { time ->
          "Rozpoczęto: $time"
        },
        map = bitmapReader.decode(encoded = params.state.event.map.imageData),
        userSessionSection = EventDetailsVM.ScreenData.MainFinished.UserSessionSection(
          sectionLabel = "Ukończono już to wydarzenie z wynikiem:",
          joinTime = dateFormatter.format(
            dateTime = params.state.sessionParticipant.joinedAt,
            format = DateFormatter.Format.DATE_TIME,
          ).let { time ->
            "Czas rozpoczęcia: $time"
          },
          finishTime = dateFormatter.format(
            dateTime = params.state.sessionParticipant.finishedAt,
            format = DateFormatter.Format.DATE_TIME,
          ).let { time ->
            "Czas zakończenia: $time"
          },
        )
      )
    }
}

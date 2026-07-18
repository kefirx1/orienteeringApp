package pl.dev.bkwiatkowski.feature.maps.presentation.eventdetails

import pl.dev.bkwiatkowski.common.core.time.DateFormatter
import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader
import pl.dev.bkwiatkowski.feature.maps.domain.model.EventStatus

interface EventDetailsMapper : Mapper<EventDetailsMapper.Params, EventDetailsVM.ScreenData> {
  data class Params(
    val state: EventDetailsVM.State,
    val onBackClick: () -> Unit,
    val onPlayClick: () -> Unit,
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
      is EventDetailsVM.State.Initialized -> EventDetailsVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        event = params.state.event,
        topAppBarData = TopAppBarData.Back(onNavigationIconClick = params.onBackClick),
        startDateTime = dateFormatter.format(
          dateTime = params.state.event.startDate,
          format = DateFormatter.Format.DATE_TIME,
        ),
        map = bitmapReader.decode(encoded = params.state.event.map.imageData),
        playButtonData = when (params.state.event.eventStatus) {
          EventStatus.CONTINUOUS,
          EventStatus.PLANNED -> LargeButtonData.Primary(
            text = "Zagraj",
            onClick = params.onPlayClick,
          )
          EventStatus.IN_PROGRESS -> LargeButtonData.Primary(
            text = "Dołącz",
            onClick = params.onPlayClick,
          )
          EventStatus.COMPLETED -> null
        }
      )
    }
}

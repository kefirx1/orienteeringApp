package pl.dev.bkwiatkowski.feature.event.presentation.map

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.icon.ZoomImageData
import pl.dev.bkwiatkowski.common.ui.image.BitmapReader

interface EventMapMapper : Mapper<EventMapMapper.Params, EventMapVM.ScreenData> {
  data class Params(
    val state: EventMapVM.State,
    val onBackClick: () -> Unit,
  )
}

class EventMapMapperImpl(
  private val bitmapReader: BitmapReader,
) : EventMapMapper {
  override fun invoke(params: EventMapMapper.Params): EventMapVM.ScreenData =
    when (params.state) {
      is EventMapVM.State.Loading -> EventMapVM.ScreenData.Loading(
        onBackClick = params.onBackClick,
      )
      is EventMapVM.State.Active -> {
        val details = params.state.eventDetails

        EventMapVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          title = details.name,
          mapData = bitmapReader.decode(encoded = details.map.imageData)?.let { bitmap ->
            ZoomImageData(
              bitmap = bitmap,
              contentDescription = "Event map",
            )
          }
        )
      }
    }
}

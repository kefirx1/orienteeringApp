package pl.dev.bkwiatkowski.feature.event.presentation.success.provider

import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDateTime
import pl.dev.bkwiatkowski.common.ui.preview.ViewModelPreviewProvider
import pl.dev.bkwiatkowski.feature.event.domain.model.FinishSessionResponse
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionParticipant
import pl.dev.bkwiatkowski.feature.event.domain.model.SessionWaypointDetail
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventVM
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventMapper
import pl.dev.bkwiatkowski.feature.event.presentation.success.SuccessEventMapperImpl

class SuccessEventPreviewProvider : ViewModelPreviewProvider<SuccessEventVM, SuccessEventVM.ScreenData, SuccessEventMapper.Params>() {
  override val mapper: SuccessEventMapper = SuccessEventMapperImpl()

  override val values: Sequence<SuccessEventVM> = sequenceOf(
    object : SuccessEventVM {
      override val screenData = MutableStateFlow(
        value = mapper(
          params = getMapperParams(
            state = SuccessEventVM.State.Active(
              finishResponse = FinishSessionResponse(
                participant = SessionParticipant(
                  sessionUuid = "",
                  joinedAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                  finishedAt = LocalDateTime.of(2024, 6, 1, 15, 0),
                ),
                sessionWaypointDetails = listOf(
                  SessionWaypointDetail(
                    waypointId = 1,
                    visitedAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                  ),
                  SessionWaypointDetail(
                    waypointId = 2,
                    visitedAt = LocalDateTime.of(2024, 6, 1, 12, 0),
                  ),
                ),
              ),
            ),
          ),
        )
      )
    }
  )

  private fun getMapperParams(state: SuccessEventVM.State): SuccessEventMapper.Params =
    SuccessEventMapper.Params(
      state = state,
      onBackClick = {},
    )
}

package pl.dev.bkwiatkowski.feature.event.presentation.main

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.permissions.PermissionRequesterData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData

interface EventMainMapper : Mapper<EventMainMapper.Params, EventMainVM.ScreenData> {
  data class Params(
    val state: EventMainVM.State,
    val onBackClick: () -> Unit,
    val onOpenMapClick: () -> Unit,
    val onOpenGameClick: () -> Unit,
    val onOpenSettingsClick: () -> Unit,
    val onRequestPermissionClick: () -> Unit,
  )
}

class EventMainMapperImpl : EventMainMapper {
  override fun invoke(params: EventMainMapper.Params): EventMainVM.ScreenData =
    when (val state = params.state) {
      is EventMainVM.State.Initial -> EventMainVM.ScreenData.Loading(
        onBackClick = params.onBackClick,
      )
      is EventMainVM.State.PermissionDenied -> EventMainVM.ScreenData.PermissionDenied(
        onBackClick = params.onBackClick,
        topAppBarData = TopAppBarData.Back(
          onNavigationIconClick = params.onBackClick,
        ),
        permissionRequesterData = PermissionRequesterData(
          label = "Aby grać musisz zezwolić na dostęp do lokalizacji",
          onOpenSettingsClick = params.onOpenSettingsClick,
          requestPermissionButtonData = SmallButtonData.Secondary(
            text = "Zezwól na lokalizację",
            onClick = params.onRequestPermissionClick,
          ),
          isDeniedForever = state.isDeniedForever,
        )
      )
      is EventMainVM.State.Active -> EventMainVM.ScreenData.Main(
        onBackClick = params.onBackClick,
        onOpenMapClick = params.onOpenMapClick,
        onOpenGameClick = params.onOpenGameClick,
        currentTab = params.state.stateData.currentTab,
        topAppBarData = TopAppBarData.Back(
          onNavigationIconClick = params.onBackClick,
        ),
        tabs = listOf(
          EventMainVM.ScreenData.Main.TabData(
            title = "Mapa",
            onClick = params.onOpenMapClick,
          ),
          EventMainVM.ScreenData.Main.TabData(
            title = "Gra",
            onClick = params.onOpenGameClick,
          ),
        )
      )
    }
}

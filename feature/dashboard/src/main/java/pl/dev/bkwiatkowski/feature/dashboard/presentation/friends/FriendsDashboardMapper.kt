package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

import pl.dev.bkwiatkowski.common.core.usecase.Mapper
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import javax.inject.Inject

interface FriendsDashboardMapper : Mapper<FriendsDashboardMapper.Params, FriendsDashboardVM.ScreenData> {
  data class Params(
    val state: FriendsDashboardVM.State,
    val onBackClick: () -> Unit,
    val onSearchTextChanged: (String) -> Unit,
    val onSearchClick: () -> Unit,
  )
}

class FriendsDashboardMapperImpl @Inject constructor() : FriendsDashboardMapper {
  override fun invoke(params: FriendsDashboardMapper.Params): FriendsDashboardVM.ScreenData =
    when (params.state) {
      is FriendsDashboardVM.State.Initial -> FriendsDashboardVM.ScreenData.Empty(
        onBackClick = params.onBackClick,
      )
      is FriendsDashboardVM.State.Active -> {
        FriendsDashboardVM.ScreenData.Main(
          onBackClick = params.onBackClick,
          topBarData = TopAppBarData.BackAndTitle(
            title = "Znajomi",
            onNavigationIconClick = params.onBackClick,
          ),
          searchFieldData = TextFieldData(
            label = "Szukaj znajomego",
            hint = "Wpisz jego nazwę",
            onValueChanged = params.onSearchTextChanged,
            initialText = params.state.content.searchText,
            validationState = params.state.content.searchTextValidation,
          ),
          searchButtonData = LargeButtonData.Primary(
            text = "Szukaj",
            onClick = params.onSearchClick,
          ),
        )
      }
    }
}

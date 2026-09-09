package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.getState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.isValid
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidateSearchTextUC
import javax.inject.Inject

interface FriendsDashboardVM {
  data class StateContent(
    val searchText: String = "",
    val searchTextValidation: ValidationState = ValidationState.UnVerified,
  )

  sealed interface State {
    data object Initial : State

    data class Active(
      val content: StateContent,
    ) : State
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data class UpdateSearchText(val searchText: String) : Action
    data object OnSearchClick : Action
    data object Back : Action
  }

  sealed interface ScreenData {
    val onBackClick: () -> Unit

    data class Empty(
      override val onBackClick: () -> Unit,
    ) : ScreenData

    data class Main(
      override val onBackClick: () -> Unit,
      val topBarData: TopAppBarData,
      val searchFieldData: TextFieldData,
      val searchButtonData: LargeButtonData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class FriendsDashboardVMImpl @Inject constructor(
  private val mapper: FriendsDashboardMapper,
  private val validateSearchTextUC: ValidateSearchTextUC,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val dashboardInteractor: DashboardInteractor,
) : CustomViewModel<FriendsDashboardVM.State, FriendsDashboardVM.ScreenData, FriendsDashboardVM.Action.Navigation>(
  initialStateValue = FriendsDashboardVM.State.Initial,
), FriendsDashboardVM {

  override val screenData: StateFlow<FriendsDashboardVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: FriendsDashboardVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is FriendsDashboardVM.State.Initial -> when (action) {
          else -> {}
        }

        is FriendsDashboardVM.State.Active -> when (action) {
          is FriendsDashboardVM.Action.UpdateSearchText -> {
            currentState.copy(
              content = currentState.content.copy(
                searchText = action.searchText,
                searchTextValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }

          is FriendsDashboardVM.Action.OnSearchClick -> runWithLoaderUC {
            val validationResult = validateSearchTextUC(
              params = ValidateSearchTextUC.Params(
                searchText = currentState.content.searchText,
              )
            ).getState()

            if (!validationResult.isValid()) {
              currentState.copy(
                content = currentState.content.copy(
                  searchTextValidation = validationResult,
                )
              ).mutate()
              return@runWithLoaderUC
            }

            dashboardInteractor.getUserByUsername(
              username = currentState.content.searchText,
            ).onRight { userResponse ->
              currentState.copy(
                content = currentState.content.copy(
                  searchTextValidation = ValidationState.Valid,
                )
              ).mutate()
            }.onLeft { _ ->
              currentState.copy(
                content = currentState.content.copy(
                  searchTextValidation = ValidationState.Invalid(message = "Użytkownik nie znaleziony"),
                )
              ).mutate()
            }
          }

          is FriendsDashboardVM.Action.Back -> {
            FriendsDashboardVM.Action.Navigation.Back.emit()
          }

          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: FriendsDashboardVM.State) {
    when (newState) {
      is FriendsDashboardVM.State.Initial -> {}
      is FriendsDashboardVM.State.Active -> {}
    }
  }

  override fun mapScreenData(): FriendsDashboardVM.ScreenData = mapper(
    params = FriendsDashboardMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(FriendsDashboardVM.Action.Back) },
      onSearchTextChanged = { text -> dispatchAction(FriendsDashboardVM.Action.UpdateSearchText(searchText = text)) },
      onSearchClick = { dispatchAction(FriendsDashboardVM.Action.OnSearchClick) },
    ),
  )
}

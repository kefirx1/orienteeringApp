package pl.dev.bkwiatkowski.feature.dashboard.presentation.friends

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.dev.bkwiatkowski.common.core.error.ErrorDataMapper
import pl.dev.bkwiatkowski.common.core.error.ErrorScreenData
import pl.dev.bkwiatkowski.common.core.loader.RunWithLoaderUC
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.common.core.viewmodel.CustomViewModel
import pl.dev.bkwiatkowski.common.ui.component.button.LargeButtonData
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.input.TextFieldData
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.getState
import pl.dev.bkwiatkowski.common.ui.component.input.ValidationState.Companion.isValid
import pl.dev.bkwiatkowski.common.ui.component.tab.TopAppBarData
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsListData
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidateSearchTextUC
import javax.inject.Inject

interface FriendsDashboardVM {
  data class StateContent(
    val searchText: String = "",
    val searchTextValidation: ValidationState = ValidationState.UnVerified,
    val friendsData: FriendsListData,
  )

  sealed interface State {
    sealed interface Initial : State {
      data object Loading : Initial

      data class Error(
        val errorScreenData: ErrorScreenData,
      ) : Initial
    }

    sealed interface Active : State {
      data class Content(
        val stateContent: StateContent
      ) : Active

      data class Error(
        val errorScreenData: ErrorScreenData,
        val stateContent: StateContent,
      ) : Active
    }
  }

  sealed interface Action {
    sealed interface Navigation : Action {
      data object Back : Navigation
    }

    data class OnAcceptFriendClick(val friendId: Int) : Action
    data class OnRemoveFriendClick(val friendId: Int) : Action
    data object LoadData : Action
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
      val friendsList: List<FriendsListItem>,
      val emptyLabel: String,
    ) : ScreenData {
      data class FriendsListItem(
        val acceptationLabel: String?,
        val username: String,
        val acceptFriendRequestButtonData: SmallButtonData?,
        val removeFriendButtonData: SmallButtonData,
      )
    }

    data class ErrorScreen(
      override val onBackClick: () -> Unit,
      val errorData: ErrorScreenData,
    ) : ScreenData
  }

  val screenData: StateFlow<ScreenData>
}

@HiltViewModel
class FriendsDashboardVMImpl @Inject constructor(
  private val mapper: FriendsDashboardMapper,
  private val errorDataMapper: ErrorDataMapper,
  private val validateSearchTextUC: ValidateSearchTextUC,
  private val runWithLoaderUC: RunWithLoaderUC,
  private val dashboardInteractor: DashboardInteractor,
) : CustomViewModel<FriendsDashboardVM.State, FriendsDashboardVM.ScreenData, FriendsDashboardVM.Action.Navigation>(
  initialStateValue = FriendsDashboardVM.State.Initial.Loading,
), FriendsDashboardVM {

  override val screenData: StateFlow<FriendsDashboardVM.ScreenData> = _screenData

  init {
    initState()
  }

  fun dispatchAction(action: FriendsDashboardVM.Action) {
    viewModelScope.launch {
      when (val currentState = state.value) {
        is FriendsDashboardVM.State.Initial.Loading -> when (action) {
          is FriendsDashboardVM.Action.Back ->
            FriendsDashboardVM.Action.Navigation.Back.emit()

          is FriendsDashboardVM.Action.LoadData -> runWithLoaderUC {
            either {
              val friendsList = dashboardInteractor.getFriendsList().getRight()

              FriendsDashboardVM.State.Active.Content(
                stateContent = FriendsDashboardVM.StateContent(
                  friendsData = friendsList,
                ),
              ).override()
            }.onLeft { error ->
              FriendsDashboardVM.State.Initial.Error(
                errorScreenData = errorDataMapper(
                  params = ErrorDataMapper.Params(
                    error = error,
                    onCloseClick = { dispatchAction(FriendsDashboardVM.Action.Back) },
                  )
                ),
              ).override()
            }
          }
          else -> {}
        }

        is FriendsDashboardVM.State.Initial.Error -> when (action) {
          is FriendsDashboardVM.Action.Back ->
            FriendsDashboardVM.Action.Navigation.Back.emit()
          else -> {}
        }

        is FriendsDashboardVM.State.Active.Content -> when (action) {
          is FriendsDashboardVM.Action.UpdateSearchText -> {
            currentState.copy(
              stateContent = currentState.stateContent.copy(
                searchText = action.searchText,
                searchTextValidation = ValidationState.UnVerified,
              )
            ).mutate()
          }

          is FriendsDashboardVM.Action.OnSearchClick -> runWithLoaderUC {
            val validationResult = validateSearchTextUC(
              params = ValidateSearchTextUC.Params(
                searchText = currentState.stateContent.searchText,
              )
            ).getState()

            if (!validationResult.isValid()) {
              currentState.copy(
                stateContent = currentState.stateContent.copy(
                  searchTextValidation = validationResult,
                )
              ).mutate()
              return@runWithLoaderUC
            }

            either {
              val userResponse = dashboardInteractor.getUserByUsername(
                username = currentState.stateContent.searchText,
              ).onLeft { _ ->
                currentState.copy(
                  stateContent = currentState.stateContent.copy(
                    searchTextValidation = ValidationState.Invalid(message = "Użytkownik nie znaleziony"),
                  )
                ).mutate()
                return@either
              }.getRight()

              currentState.copy(
                stateContent = currentState.stateContent.copy(
                  searchTextValidation = ValidationState.Valid,
                )
              ).mutate()

              dashboardInteractor.sendFriendRequest(
                friendId = userResponse.id,
              ).onRight {
                dispatchAction(FriendsDashboardVM.Action.LoadData)
              }.onLeft { error ->
                FriendsDashboardVM.State.Active.Error(
                  errorScreenData = errorDataMapper(
                    params = ErrorDataMapper.Params(
                      error = error,
                      onCloseClick = { dispatchAction(FriendsDashboardVM.Action.Back) },
                    )
                  ),
                  stateContent = currentState.stateContent,
                ).override()
                return@either
              }.getRight()
            }
          }

          is FriendsDashboardVM.Action.Back -> {
            FriendsDashboardVM.Action.Navigation.Back.emit()
          }

          is FriendsDashboardVM.Action.OnAcceptFriendClick -> runWithLoaderUC {
            either {
              dashboardInteractor.acceptFriendRequest(friendId = action.friendId).getRight()
            }.onRight {
              dispatchAction(FriendsDashboardVM.Action.LoadData)
            }.onLeft { error ->
              FriendsDashboardVM.State.Active.Error(
                errorScreenData = errorDataMapper(
                  params = ErrorDataMapper.Params(
                    error = error,
                    onCloseClick = { dispatchAction(FriendsDashboardVM.Action.Back) },
                  )
                ),
                stateContent = currentState.stateContent,
              ).override()
            }
          }

          is FriendsDashboardVM.Action.OnRemoveFriendClick -> runWithLoaderUC {
            either {
              dashboardInteractor.removeFriend(friendId = action.friendId).getRight()
            }.onRight {
              dispatchAction(FriendsDashboardVM.Action.LoadData)
            }.onLeft { error ->
              FriendsDashboardVM.State.Active.Error(
                errorScreenData = errorDataMapper(
                  params = ErrorDataMapper.Params(
                    error = error,
                    onCloseClick = { dispatchAction(FriendsDashboardVM.Action.Back) },
                  )
                ),
                stateContent = currentState.stateContent,
              ).override()
            }
          }

          is FriendsDashboardVM.Action.LoadData ->
            FriendsDashboardVM.State.Initial.Loading.override()

          else -> {}
        }

        is FriendsDashboardVM.State.Active.Error -> when (action) {
          is FriendsDashboardVM.Action.Back -> FriendsDashboardVM.State.Active.Content(
            stateContent = currentState.stateContent,
          ).override()
          else -> {}
        }
      }
    }
  }

  override suspend fun onStateEnter(newState: FriendsDashboardVM.State) {
    when (newState) {
      is FriendsDashboardVM.State.Initial.Loading -> dispatchAction(FriendsDashboardVM.Action.LoadData)
      is FriendsDashboardVM.State.Initial.Error -> {}
      is FriendsDashboardVM.State.Active.Content -> {}
      is FriendsDashboardVM.State.Active.Error -> {}
    }
  }

  override fun mapScreenData(): FriendsDashboardVM.ScreenData = mapper(
    params = FriendsDashboardMapper.Params(
      state = state.value,
      onBackClick = { dispatchAction(FriendsDashboardVM.Action.Back) },
      onSearchTextChanged = { text -> dispatchAction(FriendsDashboardVM.Action.UpdateSearchText(searchText = text)) },
      onSearchClick = { dispatchAction(FriendsDashboardVM.Action.OnSearchClick) },
      onAcceptFriendClick = { friendId -> dispatchAction(FriendsDashboardVM.Action.OnAcceptFriendClick(friendId = friendId)) },
      onRemoveFriendClick = { friendId -> dispatchAction(FriendsDashboardVM.Action.OnRemoveFriendClick(friendId = friendId)) },
    ),
  )
}

package pl.dev.bkwiatkowski.orienteeringapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.dashboard.domain.interactor.DashboardInteractor
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.CheckUserResponse
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.EventSession
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendItem
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsListData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendshipStatus
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.SessionsData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.UserSessionData
import pl.dev.bkwiatkowski.feature.event.domain.usecase.GetLastActiveSavedEventUC
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEFriendshipStatus
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendEventsRepository
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.AcceptFriendRequestUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.ChangePasswordUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.GetFriendsListUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.GetUserByUsernameUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.GetUserSessionsUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.RemoveFriendUC
import pl.dev.bkwiatkowski.technical.backend.domain.usecase.SendFriendRequestUC
import pl.dev.bkwiatkowski.technical.mobile.domain.repository.MobileSettingsRepository
import pl.dev.bkwiatkowski.technical.mobile.domain.usecase.FetchMobileSettingsUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.GetUserNameUC
import pl.dev.bkwiatkowski.technical.user.domain.usecase.LogoutUC

@Module
@InstallIn(SingletonComponent::class)
object DashboardSetupModule {

  @Provides
  fun provideDashboardMobileInteractor(
    fetchMobileSettingsUC: FetchMobileSettingsUC,
    getUserNameUC: GetUserNameUC,
    logoutUC: LogoutUC,
    changePasswordUC: ChangePasswordUC,
    getUserSessionsUC: GetUserSessionsUC,
    getUserByUsernameUC: GetUserByUsernameUC,
    getFriendsListUC: GetFriendsListUC,
    sendFriendRequestUC: SendFriendRequestUC,
    acceptFriendRequestUC: AcceptFriendRequestUC,
    removeFriendUC: RemoveFriendUC,
    mobileSettingsRepository: MobileSettingsRepository,
    backendEventsRepository: BackendEventsRepository,
    getLastActiveSavedEventUC: GetLastActiveSavedEventUC,
  ): DashboardInteractor =
    object : DashboardInteractor {
      override suspend fun fetchMobileSettings(): Either<DomainError, Unit> =
        fetchMobileSettingsUC(UseCase.Params.Empty)

      override suspend fun getLastNewMobileEventId(): Either<DomainError, Int> =
        backendEventsRepository.getLastNewMobileEventId()

      override suspend fun getUserName(): Either<DomainError, String> =
        getUserNameUC(UseCase.Params.Empty)

      override suspend fun logout(): Either<DomainError, Unit> =
        logoutUC(params = UseCase.Params.Empty)

      override suspend fun changePassword(oldPassword: String, newPassword: String): Either<DomainError, Unit> =
        changePasswordUC(params = ChangePasswordUC.Params(oldPassword = oldPassword, newPassword = newPassword))

      override suspend fun getUserSessions(): Either<DomainError, SessionsData> = either {
        val settings = mobileSettingsRepository.getMobileSettings().getRight()
        val beList =
          getUserSessionsUC(params = GetUserSessionsUC.Params(userId = settings.userId)).getRight()

        SessionsData(
          sessions = beList.map { be ->
            UserSessionData(
              sessionUuid = be.sessionUuid,
              startedAt = be.startedAt,
              visitedWaypointsCount = be.visitedWaypointsCount,
              mapName = be.mapName,
              eventName = be.eventName,
              finishedAt = be.finishedAt,
            )
          },
        )
      }

      override suspend fun getLastActiveSavedEvent(): Either<DomainError, MobileEventDetails> =
        getLastActiveSavedEventUC(params = UseCase.Params.Empty).mapRight { event ->
          MobileEventDetails(
            id = event.id,
            session = EventSession(
              id = event.session.id,
              startedAt = event.session.startedAt,
              userCanJoin = event.session.userCanJoin,
              finishedAt = event.session.finishedAt,
            ),
          )
        }

      override suspend fun getUserByUsername(username: String): Either<DomainError, CheckUserResponse> =
        getUserByUsernameUC(params = GetUserByUsernameUC.Params(username = username)).mapRight { user ->
          CheckUserResponse(
            username = user.username,
            id = user.id,
          )
        }

      override suspend fun getFriendsList(): Either<DomainError, FriendsListData> =
        getFriendsListUC(params = UseCase.Params.Empty).mapRight { response ->
          FriendsListData(
            friends = response.friends.map { friend ->
              FriendItem(
                friendId = friend.friendId,
                username = friend.username,
                createdAt = friend.createdAt,
                status = friend.status.toFeatureModel(),
                friendStatus = friend.friendStatus.toFeatureModel(),
                joinedAt = friend.joinedAt,
                attendedEventsCount = friend.attendedEventsCount,
              )
            },
          )
        }

      override suspend fun sendFriendRequest(friendId: Int): Either<DomainError, Unit> =
        sendFriendRequestUC(params = SendFriendRequestUC.Params(friendId = friendId))

      override suspend fun acceptFriendRequest(friendId: Int): Either<DomainError, Unit> =
        acceptFriendRequestUC(params = AcceptFriendRequestUC.Params(friendId = friendId))

      override suspend fun removeFriend(friendId: Int): Either<DomainError, Unit> =
        removeFriendUC(params = RemoveFriendUC.Params(friendId = friendId))

      private fun BEFriendshipStatus.toFeatureModel(): FriendshipStatus = when (this) {
        BEFriendshipStatus.ACCEPTED -> FriendshipStatus.ACCEPTED
        BEFriendshipStatus.NOT_ACCEPTED -> FriendshipStatus.NOT_ACCEPTED
      }
    }
}
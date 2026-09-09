package pl.dev.bkwiatkowski.feature.dashboard.domain.interactor

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.CheckUserResponse
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsListData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.MobileEventDetails
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.SessionsData

interface DashboardInteractor {
  suspend fun fetchMobileSettings(): Either<DomainError, Unit>
  suspend fun getLastNewMobileEventId(): Either<DomainError, Int>
  suspend fun getUserName(): Either<DomainError, String>
  suspend fun logout(): Either<DomainError, Unit>
  suspend fun changePassword(
    oldPassword: String,
    newPassword: String,
  ): Either<DomainError, Unit>
  suspend fun getUserSessions(): Either<DomainError, SessionsData>
  suspend fun getLastActiveSavedEvent(): Either<DomainError, MobileEventDetails>
  suspend fun getUserByUsername(username: String): Either<DomainError, CheckUserResponse>
  suspend fun getFriendsList(): Either<DomainError, FriendsListData>
  suspend fun sendFriendRequest(friendId: Int): Either<DomainError, Unit>
  suspend fun acceptFriendRequest(friendId: Int): Either<DomainError, Unit>
  suspend fun removeFriend(friendId: Int): Either<DomainError, Unit>
}
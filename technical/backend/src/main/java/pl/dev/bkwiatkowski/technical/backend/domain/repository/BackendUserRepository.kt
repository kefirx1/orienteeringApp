package pl.dev.bkwiatkowski.technical.backend.domain.repository

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.technical.backend.domain.model.BECheckUserResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEGetFriendsListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSession

interface BackendUserRepository {
  suspend fun getUserSessions(userId: Int): Either<DomainError, List<BEUserSession>>
  suspend fun getUserByUsername(username: String): Either<DomainError, BECheckUserResponse>
  suspend fun getFriendsList(): Either<DomainError, BEGetFriendsListResponse>
  suspend fun sendFriendRequest(friendId: Int): Either<DomainError, Unit>
  suspend fun acceptFriendRequest(friendId: Int): Either<DomainError, Unit>
  suspend fun removeFriend(friendId: Int): Either<DomainError, Unit>
}

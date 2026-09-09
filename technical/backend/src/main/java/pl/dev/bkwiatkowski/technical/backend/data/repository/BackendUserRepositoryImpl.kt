package pl.dev.bkwiatkowski.technical.backend.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.api.AcceptFriendRequest
import pl.dev.bkwiatkowski.technical.backend.api.GetFriendsList
import pl.dev.bkwiatkowski.technical.backend.api.GetMobileUserByUsername
import pl.dev.bkwiatkowski.technical.backend.api.GetUserSessions
import pl.dev.bkwiatkowski.technical.backend.api.RemoveFriend
import pl.dev.bkwiatkowski.technical.backend.api.SendFriendRequest
import pl.dev.bkwiatkowski.technical.backend.data.AcceptFriendRequestDto
import pl.dev.bkwiatkowski.technical.backend.data.CheckUserResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.GetFriendsListResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.RemoveFriendDto
import pl.dev.bkwiatkowski.technical.backend.data.SendFriendRequestDto
import pl.dev.bkwiatkowski.technical.backend.data.UserSessionsResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.backend.domain.model.BECheckUserResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEGetFriendsListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSession
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendUserRepository

class BackendUserRepositoryImpl(
  private val callMediator: CallMediator,
  private val clientFactory: HttpClientFactory,
) : BackendUserRepository {

  private val client
    get() = clientFactory.create()

  override suspend fun getUserSessions(userId: Int): Either<DomainError, List<BEUserSession>> =
    callMediator<GetUserSessions> {
      client.get(resource = GetUserSessions(userId = userId)).body()
    }.mapRight { response ->
      response.body<UserSessionsResponseDto>().sessions.map { it.toDomain() }
    }

  override suspend fun getUserByUsername(username: String): Either<DomainError, BECheckUserResponse> =
    callMediator<CheckUserResponseDto> {
      client.get(resource = GetMobileUserByUsername(username = username)).body()
    }.mapRight { response ->
      response.body<CheckUserResponseDto>().toDomain()
    }

  override suspend fun getFriendsList(): Either<DomainError, BEGetFriendsListResponse> =
    callMediator<GetFriendsList> {
      client.get(resource = GetFriendsList).body()
    }.mapRight { response ->
      response.body<GetFriendsListResponseDto>().toDomain()
    }

  override suspend fun sendFriendRequest(friendId: Int): Either<DomainError, Unit> =
    callMediator<SendFriendRequest> {
      client.post(
        resource = SendFriendRequest,
      ) {
        setBody(SendFriendRequestDto(friendId = friendId))
      }.body()
    }.mapRight { }

  override suspend fun acceptFriendRequest(friendId: Int): Either<DomainError, Unit> =
    callMediator<AcceptFriendRequest> {
      client.post(
        resource = AcceptFriendRequest,
      ) {
        setBody(AcceptFriendRequestDto(friendId = friendId))
      }.body()
    }.mapRight { }

  override suspend fun removeFriend(friendId: Int): Either<DomainError, Unit> =
    callMediator<RemoveFriend> {
      client.delete(
        resource = RemoveFriend,
      ) {
        setBody(RemoveFriendDto(friendId = friendId))
      }.body()
    }.mapRight { }
}

package pl.dev.bkwiatkowski.technical.backend.data.repository

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.network.CallMediator
import pl.dev.bkwiatkowski.common.network.HttpClientFactory
import pl.dev.bkwiatkowski.technical.backend.api.CheckUserInEventSession
import pl.dev.bkwiatkowski.technical.backend.api.FinishEventSession
import pl.dev.bkwiatkowski.technical.backend.api.GetFinishedSessionParticipants
import pl.dev.bkwiatkowski.technical.backend.api.GetMobileEventById
import pl.dev.bkwiatkowski.technical.backend.api.GetMobileEvents
import pl.dev.bkwiatkowski.technical.backend.api.GetSessionWaypointDetails
import pl.dev.bkwiatkowski.technical.backend.api.JoinEventSession
import pl.dev.bkwiatkowski.technical.backend.api.UploadSessionImage
import pl.dev.bkwiatkowski.technical.backend.data.FinishSessionResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.IsUserInSessionResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.JoinSessionRequestDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileEventDetailResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileEventListResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.SessionWaypointDetailsResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.SessionParticipantResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.UploadImageRequest
import pl.dev.bkwiatkowski.technical.backend.data.UploadImageResponse
import pl.dev.bkwiatkowski.technical.backend.data.mapper.BackendMapper.toDomain
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEFinishSessionResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BESessionWaypointDetailsResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BESessionParticipant
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUploadImageResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventDetailResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileEventListResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.BEUserSessionStatus
import pl.dev.bkwiatkowski.technical.backend.domain.repository.BackendEventsRepository

class BackendEventsRepositoryImpl(
  private val callMediator: CallMediator,
  private val clientFactory: HttpClientFactory,
) : BackendEventsRepository {
  private val client by lazy {
    clientFactory.create()
  }

  override suspend fun getMobileEventDetails(eventId: Int): Either<DomainError, MobileEventDetailResponse> =
    callMediator<GetMobileEventById> {
      client.get(resource = GetMobileEventById(id = eventId)).body()
    }.mapRight { response -> response.body<MobileEventDetailResponseDto>().toDomain() }

  override suspend fun getMobileEvents(): Either<DomainError, List<MobileEventListResponse>> =
    callMediator<GetMobileEvents> {
      client.get(resource = GetMobileEvents).body()
    }.mapRight { response ->
      response
        .body<List<MobileEventListResponseDto>>()
        .map { it.toDomain() }
    }

  override suspend fun joinEventSession(sessionUuid: String): Either<DomainError, Unit> =
    callMediator<JoinEventSession> {
      client.post(resource = JoinEventSession) {
        setBody(JoinSessionRequestDto(sessionUuid = sessionUuid))
      }
    }.mapRight { }

  override suspend fun checkUserInEventSession(sessionUuid: String): Either<DomainError, BEUserSessionStatus> =
    callMediator<CheckUserInEventSession> {
      client.get(resource = CheckUserInEventSession(sessionUuid = sessionUuid)).body()
    }.mapRight { response ->
      response.body<IsUserInSessionResponseDto>().status.toDomain()
    }

  override suspend fun uploadSessionImage(
    sessionUuid: String,
    imageBase64: String
  ): Either<DomainError, BEUploadImageResponse> =
    callMediator<UploadSessionImage> {
      client.post(resource = UploadSessionImage(sessionUuid = sessionUuid)) {
        setBody(UploadImageRequest(image = imageBase64))
      }
    }.mapRight { response ->
      response.body<UploadImageResponse>().toDomain()
    }

  override suspend fun getWaypointsVisited(
    sessionUuid: String,
  ): Either<DomainError, BESessionWaypointDetailsResponse> =
    callMediator<GetSessionWaypointDetails> {
      client.get(resource = GetSessionWaypointDetails(sessionUuid = sessionUuid)).body()
    }.mapRight { response ->
      response.body<SessionWaypointDetailsResponseDto>().toDomain()
    }

  override suspend fun finishEventSession(sessionUuid: String): Either<DomainError, BEFinishSessionResponse> =
    callMediator<FinishEventSession> {
      client.post(resource = FinishEventSession(sessionUuid = sessionUuid)).body()
    }.mapRight { response ->
      response.body<FinishSessionResponseDto>().toDomain()
    }

  override suspend fun getFinishedSessionParticipantsForUser(sessionUuid: String): Either<DomainError, List<BESessionParticipant>> =
    callMediator<GetFinishedSessionParticipants> {
      client.get(resource = GetFinishedSessionParticipants(sessionUuid = sessionUuid)).body()
    }.mapRight { response ->
      response.body<List<SessionParticipantResponseDto>>().toDomain()
    }
}
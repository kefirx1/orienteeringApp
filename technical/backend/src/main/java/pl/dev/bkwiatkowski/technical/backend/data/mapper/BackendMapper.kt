package pl.dev.bkwiatkowski.technical.backend.data.mapper

import pl.dev.bkwiatkowski.technical.backend.data.MobileSignInRequestDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileSignInResponseDto
import pl.dev.bkwiatkowski.technical.backend.data.MobileSignUpRequestDto
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInRequest
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignInResponse
import pl.dev.bkwiatkowski.technical.backend.domain.model.MobileSignUpRequest

object BackendMapper {

  fun MobileSignUpRequest.toDto(): MobileSignUpRequestDto =
    MobileSignUpRequestDto(
      username = username,
      email = email,
      password = password,
      phoneNumber = phoneNumber,
      dateOfBirth = dateOfBirth,
    )

  fun MobileSignInRequest.toDto(): MobileSignInRequestDto =
    MobileSignInRequestDto(
      username = username,
      password = password,
    )

  fun MobileSignInResponseDto.toDomain(): MobileSignInResponse =
    MobileSignInResponse(
      accessToken = accessToken,
      refreshToken = refreshToken,
      accessTokenExpiresTimestamp = accessTokenExpiresTimestamp,
      refreshTokenExpiresTimestamp = refreshTokenExpiresTimestamp,
    )
}
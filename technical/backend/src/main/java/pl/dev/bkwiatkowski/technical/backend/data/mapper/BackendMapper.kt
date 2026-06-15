package pl.dev.bkwiatkowski.technical.backend.data.mapper

import pl.dev.bkwiatkowski.technical.backend.data.MobileSignUpRequestDto
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
}
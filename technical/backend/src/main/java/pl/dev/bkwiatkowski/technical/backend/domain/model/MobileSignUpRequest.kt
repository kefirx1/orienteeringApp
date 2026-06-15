package pl.dev.bkwiatkowski.technical.backend.domain.model

import java.time.LocalDateTime

data class MobileSignUpRequest(
  val username: String,
  val email: String,
  val password: String,
  val phoneNumber: String? = null,
  val dateOfBirth: LocalDateTime? = null,
)

package pl.dev.bkwiatkowski.feature.login.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.validators.DateValidatorImpl
import java.time.LocalDateTime

class ValidateBirthdateUCTest {
  private val dateValidator = DateValidatorImpl()
  private val useCase = ValidateBirthdateUCImpl(dateValidator)

  private suspend fun runUseCase(birthdate: LocalDateTime): ValidationResult =
    useCase(params = ValidateBirthdateUC.Params(birthdate = birthdate))

  @Test
  fun `today date returns valid`() = runTest {
    val result = runUseCase(
      birthdate = LocalDateTime.now(),
    )

    assertEquals(ValidationResult.Valid, result)
  }

  @Test
  fun `future date returns invalid`() = runTest {
    val result = runUseCase(
      birthdate = LocalDateTime.now().plusDays(1),
    )

    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `past date returns valid`() = runTest {
    val result = runUseCase(
      birthdate = LocalDateTime.now().minusDays(1),
    )

    assertEquals(ValidationResult.Valid, result)
  }
}

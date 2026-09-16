package pl.dev.bkwiatkowski.feature.dashboard.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.validators.TextValidatorImpl

class ValidateConfirmPasswordUCTest {

  private val textValidator = TextValidatorImpl()
  private val useCase = ValidateConfirmPasswordUCImpl(textValidator)

  private suspend fun runUseCase(password: String, confirmPassword: String): ValidationResult =
    useCase(params = ValidateConfirmPasswordUC.Params(password = password, confirmPassword = confirmPassword))

  @Test
  fun `matching passwords returns valid`() = runTest {
    val result = runUseCase(password = "ValidPass1!", confirmPassword = "ValidPass1!")
    assertEquals(ValidationResult.Valid, result)
  }

  @Test
  fun `non-matching passwords returns invalid`() = runTest {
    val result = runUseCase(password = "ValidPass1!", confirmPassword = "DifferentPass1!")
    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `empty confirm password returns invalid`() = runTest {
    val result = runUseCase(password = "ValidPass1!", confirmPassword = "")
    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `empty both passwords returns invalid`() = runTest {
    val result = runUseCase(password = "", confirmPassword = "")
    assertTrue(result is ValidationResult.Invalid)
  }
}

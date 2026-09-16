package pl.dev.bkwiatkowski.feature.dashboard.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.validators.TextValidatorImpl

class ValidatePasswordUCTest {

  private val textValidator = TextValidatorImpl()
  private val useCase = ValidatePasswordUCImpl(textValidator)

  private suspend fun runUseCase(password: String): ValidationResult =
    useCase(params = ValidatePasswordUC.Params(password = password))

  @Test
  fun `valid password returns valid`() = runTest {
    val result = runUseCase(password = "ValidPass1!")
    assertEquals(ValidationResult.Valid, result)
  }

  @Test
  fun `missing special character returns invalid`() = runTest {
    val result = runUseCase(password = "ValidPass1")
    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `missing digit returns invalid`() = runTest {
    val result = runUseCase(password = "ValidPass!")
    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `missing lowercase letter returns invalid`() = runTest {
    val result = runUseCase(password = "VALIDPASS1!")
    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `missing uppercase letter returns invalid`() = runTest {
    val result = runUseCase(password = "validpass1!")
    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `too short password returns invalid`() = runTest {
    val result = runUseCase(password = "Pass1!")
    assertTrue(result is ValidationResult.Invalid)
  }

  @Test
  fun `empty password returns invalid`() = runTest {
    val result = runUseCase(password = "")
    assertTrue(result is ValidationResult.Invalid)
  }
}

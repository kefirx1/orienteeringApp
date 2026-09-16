package pl.dev.bkwiatkowski.feature.login.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.validators.TextValidatorImpl

class ValidateUserNameUCTest {

  private val textValidator = TextValidatorImpl()
  private val useCase = ValidateUserNameUCImpl(textValidator)

  private suspend fun runUseCase(userName: String): ValidationResult =
    useCase(params = ValidateUserNameUC.Params(userName = userName))

  @Test
  fun `valid username returns valid`() = runTest {
    val result = runUseCase(userName = "john_doe")
    assertEquals(ValidationResult.Valid, result)
  }

  @Test
  fun `empty username returns invalid`() = runTest {
    val result = runUseCase(userName = "")
    assertTrue(result is ValidationResult.Invalid)
  }
}

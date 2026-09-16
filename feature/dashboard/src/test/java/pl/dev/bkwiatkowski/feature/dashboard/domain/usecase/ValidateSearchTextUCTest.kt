package pl.dev.bkwiatkowski.feature.dashboard.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.dev.bkwiatkowski.common.core.validators.ValidationResult
import pl.dev.bkwiatkowski.common.validators.TextValidatorImpl

class ValidateSearchTextUCTest {

  private val textValidator = TextValidatorImpl()
  private val useCase = ValidateSearchTextUCImpl(textValidator)

  private suspend fun runUseCase(searchText: String): ValidationResult =
    useCase(params = ValidateSearchTextUC.Params(searchText = searchText))

  @Test
  fun `valid search text returns valid`() = runTest {
    val result = runUseCase(searchText = "event")
    assertEquals(ValidationResult.Valid, result)
  }

  @Test
  fun `empty search text returns invalid`() = runTest {
    val result = runUseCase(searchText = "")
    assertTrue(result is ValidationResult.Invalid)
  }
}

package pl.dev.bkwiatkowski.feature.login.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.common.core.validators.DateValidator
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateEmailUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateEmailUCImpl
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateUserNameUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateUserNameUCImpl
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidatePhoneUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidatePhoneUCImpl
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateBirthdateUC
import pl.dev.bkwiatkowski.feature.login.domain.usecase.ValidateBirthdateUCImpl
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapper
import pl.dev.bkwiatkowski.feature.login.presentation.login.mapper.LoginScreenMapperImpl
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.mapper.OnboardingScreenMapper
import pl.dev.bkwiatkowski.feature.login.presentation.onboarding.mapper.OnboardingScreenMapperImpl

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

  @Provides
  fun provideLoginScreenMapper(): LoginScreenMapper = LoginScreenMapperImpl()

  @Provides
  fun provideOnboardingScreenMapper(): OnboardingScreenMapper = OnboardingScreenMapperImpl()

  @Provides
  fun provideValidateUserNameUC(
    textValidator: TextValidator,
  ): ValidateUserNameUC = ValidateUserNameUCImpl(
    textValidator = textValidator,
  )

  @Provides
  fun provideValidateEmailUC(
    textValidator: TextValidator,
  ): ValidateEmailUC = ValidateEmailUCImpl(
    textValidator = textValidator,
  )

  @Provides
  fun provideValidatePhoneUC(
    textValidator: TextValidator,
  ): ValidatePhoneUC = ValidatePhoneUCImpl(
    textValidator = textValidator,
  )

  @Provides
  fun provideValidateBirthdateUC(
    dateValidator: DateValidator,
  ): ValidateBirthdateUC = ValidateBirthdateUCImpl(
    dateValidator = dateValidator,
  )
}

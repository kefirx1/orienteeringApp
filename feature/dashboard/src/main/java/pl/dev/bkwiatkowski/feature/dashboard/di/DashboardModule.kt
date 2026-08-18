package pl.dev.bkwiatkowski.feature.dashboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.GetFriendsStatsDataUC
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.GetFriendsStatsDataUCImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.main.MainDashboardMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDashboardMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDialogMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.settings.SettingsDialogMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.userprofile.UserProfileDashboardMapperImpl
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordMapper
import pl.dev.bkwiatkowski.feature.dashboard.presentation.changepassword.ChangePasswordMapperImpl
import pl.dev.bkwiatkowski.common.core.validators.TextValidator
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidatePasswordUC
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidatePasswordUCImpl
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidateConfirmPasswordUC
import pl.dev.bkwiatkowski.feature.dashboard.domain.usecase.ValidateConfirmPasswordUCImpl

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

  @Provides
  fun provideDashboardMapper(): MainDashboardMapper = MainDashboardMapperImpl()

  @Provides
  fun provideSettingsDashboardMapper(): SettingsDashboardMapper = SettingsDashboardMapperImpl()

  @Provides
  fun provideUserProfileDashboardMapper(): UserProfileDashboardMapper = UserProfileDashboardMapperImpl()

  @Provides
  fun provideChangePasswordMapper(): ChangePasswordMapper = ChangePasswordMapperImpl()

  @Provides
  fun provideGetFriendsStatsDataUC(): GetFriendsStatsDataUC = GetFriendsStatsDataUCImpl()

  @Provides
  fun provideSettingsDialogMapper(): SettingsDialogMapper = SettingsDialogMapperImpl()

  @Provides
  fun provideValidatePasswordUC(
    textValidator: TextValidator,
  ): ValidatePasswordUC = ValidatePasswordUCImpl(
    textValidator = textValidator,
  )

  @Provides
  fun provideValidateConfirmPasswordUC(
    textValidator: TextValidator,
  ): ValidateConfirmPasswordUC = ValidateConfirmPasswordUCImpl(
    textValidator = textValidator,
  )
}
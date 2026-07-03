package pl.dev.bkwiatkowski.feature.dashboard.domain.usecase

import pl.dev.bkwiatkowski.common.core.error.DomainError
import pl.dev.bkwiatkowski.common.core.usecase.Either
import pl.dev.bkwiatkowski.common.core.usecase.EitherUseCase
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.common.core.usecase.either
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendData
import pl.dev.bkwiatkowski.feature.dashboard.domain.model.FriendsStatsData

interface GetFriendsStatsDataUC : EitherUseCase<UseCase.Params.Empty, FriendsStatsData>

class GetFriendsStatsDataUCImpl : GetFriendsStatsDataUC {
  override suspend fun invoke(params: UseCase.Params.Empty): Either<DomainError, FriendsStatsData> = either {
    //TODO
    FriendsStatsData(
      friends = listOf(
        FriendData(
          id = 1,
          name = "JohnDoe",
          numberOfRuns = 5,
        ),
        FriendData(
          id = 2,
          name = "JaneSmith",
          numberOfRuns = 4,
        )
      ),
    )
  }
}
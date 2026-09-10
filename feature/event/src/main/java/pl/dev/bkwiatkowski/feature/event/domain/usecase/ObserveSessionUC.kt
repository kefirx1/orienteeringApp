package pl.dev.bkwiatkowski.feature.event.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import javax.inject.Inject

interface ObserveSessionUC : UseCase<UseCase.Params.Empty, Flow<WaypointVisitResponse>>

class ObserveSessionUCImpl @Inject constructor(
  private val eventRepository: EventRepository,
) : ObserveSessionUC {

  override suspend fun invoke(params: UseCase.Params.Empty): Flow<WaypointVisitResponse> =
    eventRepository.observeLocalVisits()
}

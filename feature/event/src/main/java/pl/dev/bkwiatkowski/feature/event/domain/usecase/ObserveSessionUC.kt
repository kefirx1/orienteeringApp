package pl.dev.bkwiatkowski.feature.event.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import pl.dev.bkwiatkowski.common.core.usecase.UseCase
import pl.dev.bkwiatkowski.feature.event.domain.interactor.EventBackendInteractor
import pl.dev.bkwiatkowski.feature.event.domain.model.WaypointVisitResponse
import pl.dev.bkwiatkowski.feature.event.domain.repository.EventRepository
import javax.inject.Inject

interface ObserveSessionUC : UseCase<UseCase.Params.Empty, Flow<WaypointVisitResponse>>

class ObserveSessionUCImpl @Inject constructor(
  private val eventBackendInteractor: EventBackendInteractor,
  private val eventRepository: EventRepository,
) : ObserveSessionUC {

  override suspend fun invoke(params: UseCase.Params.Empty): Flow<WaypointVisitResponse> =
    merge(
      eventBackendInteractor.observeSession(),
      eventRepository.observeLocalVisits(),
    )
}

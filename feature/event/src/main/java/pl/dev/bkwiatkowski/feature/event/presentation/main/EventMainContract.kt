package pl.dev.bkwiatkowski.feature.event.presentation.main

import pl.dev.bkwiatkowski.feature.event.domain.model.MobileEventDetails

interface EventMainContract {
  suspend fun setEventDetails(eventDetails: MobileEventDetails)
}
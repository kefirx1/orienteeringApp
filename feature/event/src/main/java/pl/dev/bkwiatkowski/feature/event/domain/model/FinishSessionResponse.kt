
package pl.dev.bkwiatkowski.feature.event.domain.model

data class FinishSessionResponse (
    val participant: SessionParticipant,
    val sessionWaypointDetails: List<SessionWaypointDetail>
)

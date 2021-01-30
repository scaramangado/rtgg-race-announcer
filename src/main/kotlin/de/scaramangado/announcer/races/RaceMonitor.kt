package de.scaramangado.announcer.races

import de.scaramangado.announcer.api.RacetimeHttpClient
import de.scaramangado.announcer.api.model.RaceStatus.Status.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RaceMonitor(private val httpClient: RacetimeHttpClient, private val raceManager: RaceManager) {

  @Scheduled(fixedDelay = 30000)
  fun scanForRaces() {
    raceManager.liveRaces(
        httpClient.getRacesOfCategory("oot")
            .filter { it.status.value in setOf(OPEN, INVITATIONAL, PENDING, IN_PROGRESS) }
    )
  }
}

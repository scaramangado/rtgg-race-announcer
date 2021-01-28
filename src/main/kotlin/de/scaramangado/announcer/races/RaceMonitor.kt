package de.scaramangado.announcer.races

import de.scaramangado.announcer.api.RacetimeHttpClient
import de.scaramangado.announcer.discord.DiscordManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RaceMonitor(private val httpClient: RacetimeHttpClient, private val discordManager: DiscordManager) {

  @Scheduled(fixedDelay = 30000)
  fun scanForRaces() {
  }
}

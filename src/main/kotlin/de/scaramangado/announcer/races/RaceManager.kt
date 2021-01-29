package de.scaramangado.announcer.races

import de.scaramangado.announcer.api.RacetimeProperties
import de.scaramangado.announcer.api.model.Race
import de.scaramangado.announcer.discord.DiscordManager
import de.scaramangado.announcer.discord.toEmbed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RaceManager(private val properties: RacetimeProperties, private val discordManager: DiscordManager) {

  private val logger = LoggerFactory.getLogger(RaceManager::class.java)

  private val races = mutableMapOf<String, AnnouncedRace>()

  fun liveRaces(liveRaces: Collection<Race>) {
    liveRaces
        .filter { it.slug !in races.keys }
        .forEach { announceRace(it) }
  }

  private fun announceRace(race: Race) {
    logger.info("Announce new race ${race.slug}")
    races[race.slug] = AnnouncedRace(race.slug)
    RaceConnection("${properties.websocketBase}/ws/race/${race.slug}") { updateMessage(it) }
  }

  private fun updateMessage(race: Race) {

    val announcement = races[race.slug] ?: return logMissingRace(race.slug)

    logger.trace("Update announcement ${race.slug}")

    if (announcement.messageId.isEmpty()) {
      announcement.messageId = discordManager.sendMessage(race.toEmbed())
    } else {
      discordManager.editMessage(announcement.messageId, race.toEmbed())
    }

    announcement.latestVersion = race.version
  }

  private fun logMissingRace(slug: String) {
    logger.error("Tried to update unknown race $slug")
  }
}
